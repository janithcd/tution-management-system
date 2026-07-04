package lk.janith.tuitionmanagement.repository;

import lk.janith.tuitionmanagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByEnrollmentIdAndPaymentMonthAndPaymentYear(
            Long enrollmentId,
            Integer paymentMonth,
            Integer paymentYear
    );

    List<Payment> findByPaymentMonthAndPaymentYear(Integer paymentMonth, Integer paymentYear);

    List<Payment> findByEnrollmentStudentId(Long studentId);
}