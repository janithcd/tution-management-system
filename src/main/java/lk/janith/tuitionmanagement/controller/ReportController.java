package lk.janith.tuitionmanagement.controller;

import lk.janith.tuitionmanagement.entity.Attendance;
import lk.janith.tuitionmanagement.entity.Payment;
import lk.janith.tuitionmanagement.entity.Student;
import lk.janith.tuitionmanagement.enums.AttendanceStatus;
import lk.janith.tuitionmanagement.enums.PaymentStatus;
import lk.janith.tuitionmanagement.repository.AttendanceRepository;
import lk.janith.tuitionmanagement.repository.PaymentRepository;
import lk.janith.tuitionmanagement.service.PaymentService;
import lk.janith.tuitionmanagement.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentService paymentService;
    private final StudentService studentService;

    public ReportController(
            PaymentRepository paymentRepository,
            AttendanceRepository attendanceRepository,
            PaymentService paymentService,
            StudentService studentService
    ) {
        this.paymentRepository = paymentRepository;
        this.attendanceRepository = attendanceRepository;
        this.paymentService = paymentService;
        this.studentService = studentService;
    }

    @GetMapping
    public String reportsHome() {
        return "reports/index";
    }

    @GetMapping("/monthly-income")
    public String monthlyIncomeReport(
            @RequestParam(required = false) Integer paymentMonth,
            @RequestParam(required = false) Integer paymentYear,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model
    ) {
        Integer selectedMonth = paymentMonth != null ? paymentMonth : month;
        Integer selectedYear = paymentYear != null ? paymentYear : year;

        if (selectedMonth == null) {
            selectedMonth = LocalDate.now().getMonthValue();
        }

        if (selectedYear == null) {
            selectedYear = LocalDate.now().getYear();
        }

        List<Payment> payments = paymentRepository.findByPaymentMonthAndPaymentYear(
                selectedMonth,
                selectedYear
        );

        BigDecimal monthlyIncome = paymentService.getMonthlyIncome(
                selectedMonth,
                selectedYear
        );

        long paidCount = payments.stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.PAID)
                .count();

        long partialCount = payments.stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.PARTIAL)
                .count();

        long unpaidCount = payments.stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.UNPAID)
                .count();

        model.addAttribute("payments", payments);
        model.addAttribute("monthlyIncome", monthlyIncome);
        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("selectedYear", selectedYear);

        model.addAttribute("paidCount", paidCount);
        model.addAttribute("partialCount", partialCount);
        model.addAttribute("unpaidCount", unpaidCount);

        return "reports/monthly-income";
    }

    @GetMapping("/student-payments")
    public String studentPaymentReport(
            @RequestParam(required = false) Long studentId,
            Model model
    ) {
        List<Student> students = studentService.getAllStudents();
        List<Payment> payments = Collections.emptyList();
        Student selectedStudent = null;

        if (studentId != null) {
            selectedStudent = studentService.getStudentById(studentId);
            payments = paymentRepository.findByEnrollmentStudentId(studentId);
        }

        BigDecimal totalPaid = payments.stream()
                .map(Payment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBalance = payments.stream()
                .map(Payment::getBalanceAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("students", students);
        model.addAttribute("payments", payments);
        model.addAttribute("selectedStudent", selectedStudent);
        model.addAttribute("selectedStudentId", studentId);

        model.addAttribute("totalPaid", totalPaid);
        model.addAttribute("totalBalance", totalBalance);

        return "reports/student-payments";
    }

    @GetMapping("/student-attendance")
    public String studentAttendanceReport(
            @RequestParam(required = false) Long studentId,
            Model model
    ) {
        List<Student> students = studentService.getAllStudents();
        List<Attendance> records = Collections.emptyList();
        Student selectedStudent = null;

        if (studentId != null) {
            selectedStudent = studentService.getStudentById(studentId);
            records = attendanceRepository.findByEnrollmentStudentId(studentId);
        }

        long presentCount = records.stream()
                .filter(record -> record.getStatus() == AttendanceStatus.PRESENT)
                .count();

        long absentCount = records.stream()
                .filter(record -> record.getStatus() == AttendanceStatus.ABSENT)
                .count();

        long lateCount = records.stream()
                .filter(record -> record.getStatus() == AttendanceStatus.LATE)
                .count();

        model.addAttribute("students", students);
        model.addAttribute("records", records);
        model.addAttribute("selectedStudent", selectedStudent);
        model.addAttribute("selectedStudentId", studentId);

        model.addAttribute("presentCount", presentCount);
        model.addAttribute("absentCount", absentCount);
        model.addAttribute("lateCount", lateCount);

        return "reports/student-attendance";
    }
}