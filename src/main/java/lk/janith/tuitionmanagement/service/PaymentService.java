package lk.janith.tuitionmanagement.service;

import lk.janith.tuitionmanagement.dto.PaymentDueDto;
import lk.janith.tuitionmanagement.entity.Enrollment;
import lk.janith.tuitionmanagement.entity.Payment;
import lk.janith.tuitionmanagement.enums.EnrollmentStatus;
import lk.janith.tuitionmanagement.enums.PaymentMethod;
import lk.janith.tuitionmanagement.enums.PaymentStatus;
import lk.janith.tuitionmanagement.repository.EnrollmentRepository;
import lk.janith.tuitionmanagement.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import lk.janith.tuitionmanagement.entity.Payment;
import lk.janith.tuitionmanagement.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;
    public BigDecimal getMonthlyIncome(Integer paymentMonth, Integer paymentYear) {

        return paymentRepository.findByPaymentMonthAndPaymentYear(paymentMonth, paymentYear)
                .stream()
                .map(Payment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long getPendingPaymentCount(Integer paymentMonth, Integer paymentYear) {

        return getPaymentDuesForMonth(paymentMonth, paymentYear)
                .stream()
                .filter(due -> due.getStatus() != PaymentStatus.PAID)
                .count();
    }
    public PaymentService(
            PaymentRepository paymentRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment recordPayment(
            Long enrollmentId,
            Integer paymentMonth,
            Integer paymentYear,
            BigDecimal paidAmount,
            PaymentMethod paymentMethod,
            String remarks
    ) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        BigDecimal expectedAmount = enrollment.getBatch().getMonthlyFee();

        if (paidAmount == null) {
            paidAmount = BigDecimal.ZERO;
        }

        BigDecimal balanceAmount = expectedAmount.subtract(paidAmount);

        if (balanceAmount.compareTo(BigDecimal.ZERO) < 0) {
            balanceAmount = BigDecimal.ZERO;
        }

        PaymentStatus status;

        if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            status = PaymentStatus.UNPAID;
        } else if (paidAmount.compareTo(expectedAmount) >= 0) {
            status = PaymentStatus.PAID;
        } else {
            status = PaymentStatus.PARTIAL;
        }

        Payment payment = paymentRepository
                .findByEnrollmentIdAndPaymentMonthAndPaymentYear(enrollmentId, paymentMonth, paymentYear)
                .orElse(new Payment());

        payment.setEnrollment(enrollment);
        payment.setPaymentMonth(paymentMonth);
        payment.setPaymentYear(paymentYear);
        payment.setExpectedAmount(expectedAmount);
        payment.setPaidAmount(paidAmount);
        payment.setBalanceAmount(balanceAmount);
        payment.setStatus(status);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(LocalDate.now());
        payment.setRemarks(remarks);

        return paymentRepository.save(payment);
    }

    public List<PaymentDueDto> getPaymentDuesForMonth(Integer paymentMonth, Integer paymentYear) {

        List<Enrollment> activeEnrollments = enrollmentRepository.findByStatus(EnrollmentStatus.ACTIVE);
        List<PaymentDueDto> dues = new ArrayList<>();

        for (Enrollment enrollment : activeEnrollments) {

            Payment payment = paymentRepository
                    .findByEnrollmentIdAndPaymentMonthAndPaymentYear(
                            enrollment.getId(),
                            paymentMonth,
                            paymentYear
                    )
                    .orElse(null);

            if (payment == null) {
                BigDecimal expectedAmount = enrollment.getBatch().getMonthlyFee();

                dues.add(new PaymentDueDto(
                        enrollment,
                        null,
                        expectedAmount,
                        BigDecimal.ZERO,
                        expectedAmount,
                        PaymentStatus.UNPAID
                ));
            } else {
                dues.add(new PaymentDueDto(
                        enrollment,
                        payment,
                        payment.getExpectedAmount(),
                        payment.getPaidAmount(),
                        payment.getBalanceAmount(),
                        payment.getStatus()
                ));
            }
        }

        return dues;
    }
}