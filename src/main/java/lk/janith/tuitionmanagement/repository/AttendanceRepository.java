package lk.janith.tuitionmanagement.repository;

import lk.janith.tuitionmanagement.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEnrollmentIdAndAttendanceDate(Long enrollmentId, LocalDate attendanceDate);

    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);

    List<Attendance> findByEnrollmentBatchIdAndAttendanceDate(Long batchId, LocalDate attendanceDate);

    List<Attendance> findByEnrollmentStudentId(Long studentId);

    long countByAttendanceDate(LocalDate attendanceDate);

    
}