package lk.janith.tuitionmanagement.controller;

import lk.janith.tuitionmanagement.enums.AttendanceStatus;
import lk.janith.tuitionmanagement.service.AttendanceService;
import lk.janith.tuitionmanagement.service.BatchService;
import lk.janith.tuitionmanagement.service.EnrollmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import lk.janith.tuitionmanagement.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @GetMapping("/records")
    public String showAttendanceRecords(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long batchId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        LocalDate selectedFromDate = null;
        LocalDate selectedToDate = null;
        AttendanceStatus selectedStatus = null;

        if (fromDate != null && !fromDate.isBlank()) {
            selectedFromDate = LocalDate.parse(fromDate);
        }

        if (toDate != null && !toDate.isBlank()) {
            selectedToDate = LocalDate.parse(toDate);
        }

        if (status != null && !status.isBlank()) {
            selectedStatus = AttendanceStatus.valueOf(status);
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "attendanceDate", "id")
        );

        Page<Attendance> attendancePage = attendanceService.searchAttendanceRecords(
                keyword,
                batchId,
                selectedFromDate,
                selectedToDate,
                selectedStatus,
                pageable
        );

        model.addAttribute("records", attendancePage.getContent());
        model.addAttribute("attendancePage", attendancePage);

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedBatchId", batchId);
        model.addAttribute("selectedFromDate", fromDate);
        model.addAttribute("selectedToDate", toDate);
        model.addAttribute("selectedStatus", status);

        model.addAttribute("batches", batchService.getAllBatches());
        model.addAttribute("statuses", AttendanceStatus.values());

        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", attendancePage.getTotalPages());
        model.addAttribute("totalItems", attendancePage.getTotalElements());

        return "attendance/records";
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