package lk.janith.tuitionmanagement.controller;

import lk.janith.tuitionmanagement.entity.Attendance;
import lk.janith.tuitionmanagement.entity.Payment;
import lk.janith.tuitionmanagement.enums.AttendanceStatus;
import lk.janith.tuitionmanagement.enums.BatchStatus;
import lk.janith.tuitionmanagement.enums.EnrollmentStatus;
import lk.janith.tuitionmanagement.enums.PaymentStatus;
import lk.janith.tuitionmanagement.enums.StudentStatus;
import lk.janith.tuitionmanagement.repository.AttendanceRepository;
import lk.janith.tuitionmanagement.repository.BatchRepository;
import lk.janith.tuitionmanagement.repository.EnrollmentRepository;
import lk.janith.tuitionmanagement.repository.PaymentRepository;
import lk.janith.tuitionmanagement.repository.StudentRepository;
import lk.janith.tuitionmanagement.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    private final StudentRepository studentRepository;
    private final BatchRepository batchRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public DashboardController(
            StudentRepository studentRepository,
            BatchRepository batchRepository,
            EnrollmentRepository enrollmentRepository,
            AttendanceRepository attendanceRepository,
            PaymentRepository paymentRepository,
            PaymentService paymentService
    ) {
        this.studentRepository = studentRepository;
        this.batchRepository = batchRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceRepository = attendanceRepository;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {

        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        long totalStudents = studentRepository.count();
        long activeStudents = studentRepository.countByStatus(StudentStatus.ACTIVE);

        long totalBatches = batchRepository.count();
        long activeBatches = batchRepository.countByStatus(BatchStatus.ACTIVE);

        long activeEnrollments = enrollmentRepository.countByStatus(EnrollmentStatus.ACTIVE);

        long todayAttendance = attendanceRepository.countByAttendanceDate(today);

        BigDecimal monthlyIncome = paymentService.getMonthlyIncome(currentMonth, currentYear);
        long pendingPayments = paymentService.getPendingPaymentCount(currentMonth, currentYear);

        List<Attendance> todayAttendanceRecords = attendanceRepository.findByAttendanceDate(today);

        long presentCount = todayAttendanceRecords.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                .count();

        long absentCount = todayAttendanceRecords.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
                .count();

        long lateCount = todayAttendanceRecords.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.LATE)
                .count();

        List<Payment> currentMonthPayments = paymentRepository.findByPaymentMonthAndPaymentYear(
                currentMonth,
                currentYear
        );

        long paidCount = currentMonthPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .count();

        long partialCount = currentMonthPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PARTIAL)
                .count();

        long unpaidCount = currentMonthPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.UNPAID)
                .count();

        List<String> monthLabels = new ArrayList<>();
        List<BigDecimal> incomeValues = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            monthLabels.add(Month.of(month).name().substring(0, 3));
            incomeValues.add(paymentService.getMonthlyIncome(month, currentYear));
        }

        model.addAttribute("today", today);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("currentYear", currentYear);

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("activeStudents", activeStudents);
        model.addAttribute("totalBatches", totalBatches);
        model.addAttribute("activeBatches", activeBatches);
        model.addAttribute("activeEnrollments", activeEnrollments);
        model.addAttribute("todayAttendance", todayAttendance);
        model.addAttribute("monthlyIncome", monthlyIncome);
        model.addAttribute("pendingPayments", pendingPayments);

        model.addAttribute("presentCount", presentCount);
        model.addAttribute("absentCount", absentCount);
        model.addAttribute("lateCount", lateCount);

        model.addAttribute("paidCount", paidCount);
        model.addAttribute("partialCount", partialCount);
        model.addAttribute("unpaidCount", unpaidCount);

        model.addAttribute("monthLabels", monthLabels);
        model.addAttribute("incomeValues", incomeValues);

        return "dashboard";
    }
}