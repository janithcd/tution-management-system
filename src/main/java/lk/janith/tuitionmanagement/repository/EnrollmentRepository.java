package lk.janith.tuitionmanagement.repository;

import lk.janith.tuitionmanagement.entity.Enrollment;
import lk.janith.tuitionmanagement.enums.EducationLevel;
import lk.janith.tuitionmanagement.enums.EnrollmentStatus;
import lk.janith.tuitionmanagement.enums.Grade;
import lk.janith.tuitionmanagement.enums.StreamType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudentIdAndBatchId(Long studentId, Long batchId);

    List<Enrollment> findByBatchIdAndStatus(Long batchId, EnrollmentStatus status);

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByStatus(EnrollmentStatus status);

    long countByStatus(EnrollmentStatus status);

    @Query("""
            SELECT e FROM Enrollment e
            WHERE
            (
                :keyword IS NULL OR :keyword = '' OR
                LOWER(e.student.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(e.student.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(e.batch.batchName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(e.batch.subject) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND (:educationLevel IS NULL OR e.batch.educationLevel = :educationLevel)
            AND (:grade IS NULL OR e.batch.grade = :grade)
            AND (:stream IS NULL OR e.batch.stream = :stream)
            AND (:status IS NULL OR e.status = :status)
            """)
    Page<Enrollment> searchEnrollments(
            @Param("keyword") String keyword,
            @Param("educationLevel") EducationLevel educationLevel,
            @Param("grade") Grade grade,
            @Param("stream") StreamType stream,
            @Param("status") EnrollmentStatus status,
            Pageable pageable
    );
}