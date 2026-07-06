package lk.janith.tuitionmanagement.service;

import lk.janith.tuitionmanagement.entity.Attendance;
import lk.janith.tuitionmanagement.entity.Payment;
import lk.janith.tuitionmanagement.repository.AttendanceRepository;
import lk.janith.tuitionmanagement.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReportService {

    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;

    public ReportService(
            PaymentRepository paymentRepository,
            AttendanceRepository attendanceRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public List<Payment> getMonthlyPayments(Integer month, Integer year) {
        return paymentRepository.findByPaymentMonthAndPaymentYear(month, year);
    }

    public BigDecimal getMonthlyIncome(Integer month, Integer year) {
        return getMonthlyPayments(month, year)
                .stream()
                .map(Payment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Payment> getStudentPaymentHistory(Long studentId) {
        return paymentRepository.findByEnrollmentStudentId(studentId);
    }

    public List<Attendance> getStudentAttendanceHistory(Long studentId) {
        return attendanceRepository.findByEnrollmentStudentId(studentId);
    }
}