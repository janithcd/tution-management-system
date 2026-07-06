package lk.janith.tuitionmanagement.controller;

import lk.janith.tuitionmanagement.service.ReportService;
import lk.janith.tuitionmanagement.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final StudentService studentService;

    public ReportController(
            ReportService reportService,
            StudentService studentService
    ) {
        this.reportService = reportService;
        this.studentService = studentService;
    }

    @GetMapping
    public String reportsHome() {
        return "reports/index";
    }

    @GetMapping("/monthly-income")
    public String monthlyIncomeReport(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model
    ) {
        if (month == null) {
            month = LocalDate.now().getMonthValue();
        }

        if (year == null) {
            year = LocalDate.now().getYear();
        }

        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);
        model.addAttribute("payments", reportService.getMonthlyPayments(month, year));
        model.addAttribute("totalIncome", reportService.getMonthlyIncome(month, year));

        return "reports/monthly-income";
    }

    @GetMapping("/student-payments")
    public String studentPaymentHistory(
            @RequestParam(required = false) Long studentId,
            Model model
    ) {
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("selectedStudentId", studentId);

        if (studentId != null) {
            model.addAttribute("payments", reportService.getStudentPaymentHistory(studentId));
        }

        return "reports/student-payments";
    }

    @GetMapping("/student-attendance")
    public String studentAttendanceHistory(
            @RequestParam(required = false) Long studentId,
            Model model
    ) {
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("selectedStudentId", studentId);

        if (studentId != null) {
            model.addAttribute("attendanceRecords", reportService.getStudentAttendanceHistory(studentId));
        }

        return "reports/student-attendance";
    }
}