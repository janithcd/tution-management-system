package lk.janith.tuitionmanagement.repository;

import lk.janith.tuitionmanagement.entity.Payment;
import lk.janith.tuitionmanagement.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
            SELECT p FROM Payment p
            WHERE
            (
                :keyword IS NULL OR :keyword = '' OR
                LOWER(p.enrollment.student.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(p.enrollment.student.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(p.enrollment.batch.batchName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND (:paymentMonth IS NULL OR p.paymentMonth = :paymentMonth)
            AND (:paymentYear IS NULL OR p.paymentYear = :paymentYear)
            AND (:status IS NULL OR p.status = :status)
            """)
    Page<Payment> searchPayments(
            @Param("keyword") String keyword,
            @Param("paymentMonth") Integer paymentMonth,
            @Param("paymentYear") Integer paymentYear,
            @Param("status") PaymentStatus status,
            Pageable pageable
    );
    @Query("""
        SELECT p FROM Payment p
        WHERE
        (
            :keyword IS NULL OR :keyword = '' OR
            LOWER(p.enrollment.student.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(p.enrollment.student.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(p.enrollment.batch.batchName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND (:paymentMonth IS NULL OR p.paymentMonth = :paymentMonth)
        AND (:paymentYear IS NULL OR p.paymentYear = :paymentYear)
        AND (:status IS NULL OR p.status = :status)
        ORDER BY p.id DESC
        """)
    List<Payment> searchPaymentsForExport(
            @Param("keyword") String keyword,
            @Param("paymentMonth") Integer paymentMonth,
            @Param("paymentYear") Integer paymentYear,
            @Param("status") PaymentStatus status
    );
}