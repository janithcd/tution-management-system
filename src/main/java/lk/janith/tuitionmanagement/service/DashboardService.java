package lk.janith.tuitionmanagement.service;

import lk.janith.tuitionmanagement.dto.PaymentDueDto;
import lk.janith.tuitionmanagement.entity.Attendance;
import lk.janith.tuitionmanagement.entity.Payment;
import lk.janith.tuitionmanagement.enums.*;
import lk.janith.tuitionmanagement.repository.AttendanceRepository;
import lk.janith.tuitionmanagement.repository.BatchRepository;
import lk.janith.tuitionmanagement.repository.EnrollmentRepository;
import lk.janith.tuitionmanagement.repository.PaymentRepository;
import lk.janith.tuitionmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final BatchRepository batchRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public DashboardService(
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

    public long getTotalStudents() {
        return studentRepository.count();
    }

    public long getActiveStudents() {
        return studentRepository.countByStatus(StudentStatus.ACTIVE);
    }

    public long getTotalBatches() {
        return batchRepository.count();
    }

    public long getActiveBatches() {
        return batchRepository.countByStatus(BatchStatus.ACTIVE);
    }

    public long getActiveEnrollments() {
        return enrollmentRepository.countByStatus(EnrollmentStatus.ACTIVE);
    }

    public long getTodayAttendanceCount() {
        return attendanceRepository.countByAttendanceDate(LocalDate.now());
    }

    public BigDecimal getThisMonthIncome() {
        LocalDate today = LocalDate.now();
        return paymentService.getMonthlyIncome(today.getMonthValue(), today.getYear());
    }

    public long getThisMonthPendingPayments() {
        LocalDate today = LocalDate.now();
        return paymentService.getPendingPaymentCount(today.getMonthValue(), today.getYear());
    }

    public Map<String, Long> getPaymentStatusChartData() {
        LocalDate today = LocalDate.now();

        List<PaymentDueDto> dues = paymentService.getPaymentDuesForMonth(
                today.getMonthValue(),
                today.getYear()
        );

        long paid = dues.stream()
                .filter(due -> due.getStatus() == PaymentStatus.PAID)
                .count();

        long partial = dues.stream()
                .filter(due -> due.getStatus() == PaymentStatus.PARTIAL)
                .count();

        long unpaid = dues.stream()
                .filter(due -> due.getStatus() == PaymentStatus.UNPAID)
                .count();

        Map<String, Long> data = new LinkedHashMap<>();
        data.put("Paid", paid);
        data.put("Partial", partial);
        data.put("Unpaid", unpaid);

        return data;
    }

    public Map<String, BigDecimal> getMonthlyIncomeByBatchChartData() {
        LocalDate today = LocalDate.now();

        List<Payment> payments = paymentRepository.findByPaymentMonthAndPaymentYear(
                today.getMonthValue(),
                today.getYear()
        );

        Map<String, BigDecimal> data = new LinkedHashMap<>();

        for (Payment payment : payments) {
            String batchName = payment.getEnrollment().getBatch().getBatchName();
            BigDecimal paidAmount = payment.getPaidAmount();

            data.put(
                    batchName,
                    data.getOrDefault(batchName, BigDecimal.ZERO).add(paidAmount)
            );
        }

        return data;
    }

    public Map<String, Long> getTodayAttendanceChartData() {
        LocalDate today = LocalDate.now();

        List<Attendance> records = attendanceRepository.findByAttendanceDate(today);

        long present = records.stream()
                .filter(record -> record.getStatus() == AttendanceStatus.PRESENT)
                .count();

        long absent = records.stream()
                .filter(record -> record.getStatus() == AttendanceStatus.ABSENT)
                .count();

        long late = records.stream()
                .filter(record -> record.getStatus() == AttendanceStatus.LATE)
                .count();

        Map<String, Long> data = new LinkedHashMap<>();
        data.put("Present", present);
        data.put("Absent", absent);
        data.put("Late", late);

        return data;
    }
}