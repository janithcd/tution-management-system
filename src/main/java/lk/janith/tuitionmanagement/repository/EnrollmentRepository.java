package lk.janith.tuitionmanagement.repository;

import lk.janith.tuitionmanagement.entity.Enrollment;
import lk.janith.tuitionmanagement.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudentIdAndBatchId(Long studentId, Long batchId);

    List<Enrollment> findByBatchIdAndStatus(Long batchId, EnrollmentStatus status);

    List<Enrollment> findByStudentId(Long studentId);
}