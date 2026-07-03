package lk.janith.tuitionmanagement.controller;

import lk.janith.tuitionmanagement.enums.AttendanceStatus;
import lk.janith.tuitionmanagement.service.AttendanceService;
import lk.janith.tuitionmanagement.service.BatchService;
import lk.janith.tuitionmanagement.service.EnrollmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final BatchService batchService;
    private final EnrollmentService enrollmentService;

    public AttendanceController(
            AttendanceService attendanceService,
            BatchService batchService,
            EnrollmentService enrollmentService
    ) {
        this.attendanceService = attendanceService;
        this.batchService = batchService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public String showAttendancePage(Model model) {
        model.addAttribute("batches", batchService.getAllBatches());
        model.addAttribute("selectedDate", LocalDate.now());
        return "attendance/select";
    }

    @GetMapping("/mark")
    public String showMarkAttendancePage(
            @RequestParam Long batchId,
            @RequestParam String date,
            Model model
    ) {
        LocalDate attendanceDate = LocalDate.parse(date);

        model.addAttribute("batch", batchService.getBatchById(batchId));
        model.addAttribute("batchId", batchId);
        model.addAttribute("selectedDate", attendanceDate);
        model.addAttribute("enrollments", enrollmentService.getActiveEnrollmentsByBatchId(batchId));
        model.addAttribute("statuses", AttendanceStatus.values());

        return "attendance/mark";
    }

    @PostMapping("/save")
    public String saveAttendance(
            @RequestParam Long batchId,
            @RequestParam String attendanceDate,
            @RequestParam("enrollmentIds") List<Long> enrollmentIds,
            @RequestParam Map<String, String> params
    ) {
        LocalDate date = LocalDate.parse(attendanceDate);

        for (Long enrollmentId : enrollmentIds) {
            String statusValue = params.get("status_" + enrollmentId);

            if (statusValue != null) {
                AttendanceStatus status = AttendanceStatus.valueOf(statusValue);
                attendanceService.saveAttendance(enrollmentId, date, status);
            }
        }

        return "redirect:/attendance/report?batchId=" + batchId + "&date=" + attendanceDate;
    }

    @GetMapping("/report")
    public String showAttendanceReport(
            @RequestParam Long batchId,
            @RequestParam String date,
            Model model
    ) {
        LocalDate attendanceDate = LocalDate.parse(date);

        model.addAttribute("batch", batchService.getBatchById(batchId));
        model.addAttribute("selectedDate", attendanceDate);
        model.addAttribute("records", attendanceService.getAttendanceByBatchAndDate(batchId, attendanceDate));

        return "attendance/report";
    }
}