package lk.janith.tuitionmanagement.repository;

import lk.janith.tuitionmanagement.entity.Attendance;
import lk.janith.tuitionmanagement.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEnrollmentIdAndAttendanceDate(Long enrollmentId, LocalDate attendanceDate);

    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);

    List<Attendance> findByEnrollmentBatchIdAndAttendanceDate(Long batchId, LocalDate attendanceDate);

    List<Attendance> findByEnrollmentStudentId(Long studentId);

    long countByAttendanceDate(LocalDate attendanceDate);

    @Query("""
            SELECT a FROM Attendance a
            WHERE
            (
                :keyword IS NULL OR :keyword = '' OR
                LOWER(a.enrollment.student.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(a.enrollment.student.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(a.enrollment.batch.batchName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(a.enrollment.batch.subject) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND (:batchId IS NULL OR a.enrollment.batch.id = :batchId)
            AND (:fromDate IS NULL OR a.attendanceDate >= :fromDate)
            AND (:toDate IS NULL OR a.attendanceDate <= :toDate)
            AND (:status IS NULL OR a.status = :status)
            """)
    Page<Attendance> searchAttendanceRecords(
            @Param("keyword") String keyword,
            @Param("batchId") Long batchId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("status") AttendanceStatus status,
            Pageable pageable
    );
}