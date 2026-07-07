package lk.janith.tuitionmanagement.service;

import lk.janith.tuitionmanagement.entity.Attendance;
import lk.janith.tuitionmanagement.entity.Enrollment;
import lk.janith.tuitionmanagement.enums.AttendanceStatus;
import lk.janith.tuitionmanagement.repository.AttendanceRepository;
import lk.janith.tuitionmanagement.repository.EnrollmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AttendanceService(
            AttendanceRepository attendanceRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.attendanceRepository = attendanceRepository;
        this.enrollmentRepository = enrollmentRepository;
    }
    public List<Attendance> searchAttendanceRecordsForExport(
            String keyword,
            Long batchId,
            LocalDate fromDate,
            LocalDate toDate,
            AttendanceStatus status
    ) {
        return attendanceRepository.searchAttendanceRecordsForExport(
                keyword,
                batchId,
                fromDate,
                toDate,
                status
        );
    }
    public Page<Attendance> searchAttendanceRecords(
            String keyword,
            Long batchId,
            LocalDate fromDate,
            LocalDate toDate,
            AttendanceStatus status,
            Pageable pageable
    ) {
        return attendanceRepository.searchAttendanceRecords(
                keyword,
                batchId,
                fromDate,
                toDate,
                status,
                pageable
        );
    }
    public void saveAttendance(Long enrollmentId, LocalDate attendanceDate, AttendanceStatus status) {

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        Attendance attendance = attendanceRepository
                .findByEnrollmentIdAndAttendanceDate(enrollmentId, attendanceDate)
                .orElse(new Attendance());

        attendance.setEnrollment(enrollment);
        attendance.setAttendanceDate(attendanceDate);
        attendance.setStatus(status);
        attendance.setMarkedAt(LocalDateTime.now());

        attendanceRepository.save(attendance);
    }

    public List<Attendance> getAttendanceByBatchAndDate(Long batchId, LocalDate date) {
        return attendanceRepository.findByEnrollmentBatchIdAndAttendanceDate(batchId, date);
    }

    public List<Attendance> getStudentAttendance(Long studentId) {
        return attendanceRepository.findByEnrollmentStudentId(studentId);
    }
}