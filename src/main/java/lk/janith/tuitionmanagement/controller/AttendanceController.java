package lk.janith.tuitionmanagement.controller;

import jakarta.servlet.http.HttpServletResponse;
import lk.janith.tuitionmanagement.entity.Attendance;
import lk.janith.tuitionmanagement.entity.Enrollment;
import lk.janith.tuitionmanagement.enums.AttendanceStatus;
import lk.janith.tuitionmanagement.service.AttendanceService;
import lk.janith.tuitionmanagement.service.BatchService;
import lk.janith.tuitionmanagement.service.EnrollmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
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

    // Attendance select page
    @GetMapping
    public String showAttendanceSelectPage(Model model) {
        model.addAttribute("batches", batchService.getAllBatches());
        model.addAttribute("today", LocalDate.now());

        return "attendance/select";
    }

    // Attendance marking page
    @GetMapping("/mark")
    public String showMarkAttendancePage(
            @RequestParam Long batchId,
            @RequestParam String attendanceDate,
            Model model
    ) {
        LocalDate selectedDate = parseDateOrToday(attendanceDate);

        List<Enrollment> enrollments = enrollmentService.getActiveEnrollmentsByBatchId(batchId);
        List<Attendance> existingAttendance = attendanceService.getAttendanceByBatchAndDate(batchId, selectedDate);

        Map<Long, AttendanceStatus> attendanceMap = new HashMap<>();

        for (Attendance attendance : existingAttendance) {
            attendanceMap.put(
                    attendance.getEnrollment().getId(),
                    attendance.getStatus()
            );
        }

        model.addAttribute("batchId", batchId);
        model.addAttribute("attendanceDate", selectedDate);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("attendanceMap", attendanceMap);
        model.addAttribute("statuses", AttendanceStatus.values());

        return "attendance/mark";
    }

    // Save marked attendance
    @PostMapping("/save")
    public String saveAttendance(
            @RequestParam Long batchId,
            @RequestParam String attendanceDate,
            @RequestParam Map<String, String> formData
    ) {
        LocalDate selectedDate = parseDateOrToday(attendanceDate);

        for (Map.Entry<String, String> entry : formData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null || value.isBlank()) {
                continue;
            }

            Long enrollmentId = null;

            // Supports input name="attendance_1"
            if (key.startsWith("attendance_")) {
                enrollmentId = Long.parseLong(key.replace("attendance_", ""));
            }

            // Also supports input name="status_1"
            if (key.startsWith("status_")) {
                enrollmentId = Long.parseLong(key.replace("status_", ""));
            }

            if (enrollmentId != null) {
                AttendanceStatus status = AttendanceStatus.valueOf(value);
                attendanceService.saveAttendance(enrollmentId, selectedDate, status);
            }
        }

        return "redirect:/attendance/report?batchId=" + batchId + "&attendanceDate=" + selectedDate;
    }

    // Attendance report by batch and date
    @GetMapping("/report")
    public String showAttendanceReport(
            @RequestParam(required = false) Long batchId,
            @RequestParam(required = false) String attendanceDate,
            Model model
    ) {
        LocalDate selectedDate = parseDateOrToday(attendanceDate);

        List<Attendance> attendanceRecords = Collections.emptyList();

        if (batchId != null) {
            attendanceRecords = attendanceService.getAttendanceByBatchAndDate(batchId, selectedDate);
        }

        model.addAttribute("batches", batchService.getAllBatches());
        model.addAttribute("records", attendanceRecords);
        model.addAttribute("selectedBatchId", batchId);
        model.addAttribute("attendanceDate", selectedDate);

        return "attendance/report";
    }

    // Full attendance records page with filters and better pagination
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

        int totalPages = attendancePage.getTotalPages();

        int startPage = Math.max(0, page - 2);
        int endPage = Math.min(totalPages - 1, page + 2);

        if (totalPages > 0 && endPage - startPage < 4) {
            if (startPage == 0) {
                endPage = Math.min(totalPages - 1, startPage + 4);
            } else if (endPage == totalPages - 1) {
                startPage = Math.max(0, endPage - 4);
            }
        }

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
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", attendancePage.getTotalElements());

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "attendance/records";
    }

    // Export filtered attendance records to CSV
    @GetMapping("/records/export")
    public void exportAttendanceRecordsToCsv(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long batchId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String status,
            HttpServletResponse response
    ) throws IOException {

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

        List<Attendance> records = attendanceService.searchAttendanceRecordsForExport(
                keyword,
                batchId,
                selectedFromDate,
                selectedToDate,
                selectedStatus
        );

        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=attendance-records.csv");

        PrintWriter writer = response.getWriter();

        writer.println("ID,Date,Student Code,Student Name,Batch,Subject,Status,Marked At,Remarks");

        for (Attendance record : records) {
            writer.println(
                    csv(record.getId()) + "," +
                            csv(record.getAttendanceDate()) + "," +
                            csv(record.getEnrollment().getStudent().getStudentCode()) + "," +
                            csv(record.getEnrollment().getStudent().getFullName()) + "," +
                            csv(record.getEnrollment().getBatch().getBatchName()) + "," +
                            csv(record.getEnrollment().getBatch().getSubject()) + "," +
                            csv(record.getStatus()) + "," +
                            csv(record.getMarkedAt()) + "," +
                            csv(record.getRemarks())
            );
        }

        writer.flush();
    }

    private LocalDate parseDateOrToday(String date) {
        if (date == null || date.isBlank()) {
            return LocalDate.now();
        }

        return LocalDate.parse(date);
    }

    private String csv(Object value) {
        if (value == null) {
            return "";
        }

        String text = value.toString();
        text = text.replace("\"", "\"\"");

        return "\"" + text + "\"";
    }
}