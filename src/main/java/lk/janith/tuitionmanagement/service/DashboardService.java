package lk.janith.tuitionmanagement.service;

import lk.janith.tuitionmanagement.enums.BatchStatus;
import lk.janith.tuitionmanagement.enums.EnrollmentStatus;
import lk.janith.tuitionmanagement.enums.StudentStatus;
import lk.janith.tuitionmanagement.repository.AttendanceRepository;
import lk.janith.tuitionmanagement.repository.BatchRepository;
import lk.janith.tuitionmanagement.repository.EnrollmentRepository;
import lk.janith.tuitionmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final BatchRepository batchRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentService paymentService;

    public DashboardService(
            StudentRepository studentRepository,
            BatchRepository batchRepository,
            EnrollmentRepository enrollmentRepository,
            AttendanceRepository attendanceRepository,
            PaymentService paymentService
    ) {
        this.studentRepository = studentRepository;
        this.batchRepository = batchRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceRepository = attendanceRepository;
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
}