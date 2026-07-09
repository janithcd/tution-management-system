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
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


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
    public Page<Payment> getPaymentPage(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }
    public Page<Payment> searchPayments(
            String keyword,
            Integer paymentMonth,
            Integer paymentYear,
            PaymentStatus status,
            Pageable pageable
    ) {
        return paymentRepository.searchPayments(keyword, paymentMonth, paymentYear, status, pageable);
    }
    public List<Payment> searchPaymentsForExport(
            String keyword,
            Integer paymentMonth,
            Integer paymentYear,
            PaymentStatus status
    ) {
        return paymentRepository.searchPaymentsForExport(
                keyword,
                paymentMonth,
                paymentYear,
                status
        );
    }

    public long getPendingPaymentCount(Integer paymentMonth, Integer paymentYear) {

        return getPaymentDuesForMonth(paymentMonth, paymentYear)
                .stream()
                .filter(due -> due.getStatus() != PaymentStatus.PAID)
                .count();
    }
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
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
        if (enrollmentId == null) {
            throw new IllegalArgumentException("Please select an enrollment.");
        }

        if (paymentMonth == null) {
            throw new IllegalArgumentException("Please select payment month.");
        }

        if (paymentYear == null) {
            throw new IllegalArgumentException("Please enter payment year.");
        }

        if (paidAmount == null || paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Paid amount must be greater than 0.");
        }

        if (paymentMethod == null) {
            throw new IllegalArgumentException("Please select payment method.");
        }

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Selected enrollment not found."));

        paymentRepository
                .findByEnrollmentIdAndPaymentMonthAndPaymentYear(enrollmentId, paymentMonth, paymentYear)
                .ifPresent(existingPayment -> {
                    throw new IllegalStateException(
                            "This enrollment already has a payment record for "
                                    + getMonthName(paymentMonth) + " " + paymentYear + "."
                    );
                });

        BigDecimal expectedAmount = enrollment.getBatch().getMonthlyFee();
        BigDecimal balanceAmount = expectedAmount.subtract(paidAmount);

        Payment payment = new Payment();
        payment.setEnrollment(enrollment);
        payment.setPaymentMonth(paymentMonth);
        payment.setPaymentYear(paymentYear);
        payment.setExpectedAmount(expectedAmount);
        payment.setPaidAmount(paidAmount);
        payment.setBalanceAmount(balanceAmount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(LocalDate.now());
        payment.setRemarks(remarks);
        payment.setStatus(calculatePaymentStatus(expectedAmount, paidAmount));

        return paymentRepository.save(payment);
    }
    private PaymentStatus calculatePaymentStatus(BigDecimal expectedAmount, BigDecimal paidAmount) {
        if (paidAmount.compareTo(expectedAmount) >= 0) {
            return PaymentStatus.PAID;
        }

        if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            return PaymentStatus.PARTIAL;
        }

        return PaymentStatus.UNPAID;
    }

    private String getMonthName(Integer month) {
        return switch (month) {
            case 1 -> "January";
            case 2 -> "February";
            case 3 -> "March";
            case 4 -> "April";
            case 5 -> "May";
            case 6 -> "June";
            case 7 -> "July";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "Month " + month;
        };
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