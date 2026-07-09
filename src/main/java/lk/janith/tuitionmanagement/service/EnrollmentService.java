package lk.janith.tuitionmanagement.service;

import lk.janith.tuitionmanagement.entity.Batch;
import lk.janith.tuitionmanagement.entity.Enrollment;
import lk.janith.tuitionmanagement.entity.Student;
import lk.janith.tuitionmanagement.enums.EducationLevel;
import lk.janith.tuitionmanagement.enums.EnrollmentStatus;
import lk.janith.tuitionmanagement.enums.Grade;
import lk.janith.tuitionmanagement.enums.StreamType;
import lk.janith.tuitionmanagement.repository.BatchRepository;
import lk.janith.tuitionmanagement.repository.EnrollmentRepository;
import lk.janith.tuitionmanagement.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final BatchRepository batchRepository;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            StudentRepository studentRepository,
            BatchRepository batchRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.batchRepository = batchRepository;
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public Enrollment getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
    }

    @Transactional
    public Enrollment enrollStudent(Long studentId, Long batchId, LocalDate enrolledDate) {

        if (studentId == null) {
            throw new IllegalArgumentException("Please select a student.");
        }

        if (batchId == null) {
            throw new IllegalArgumentException("Please select a batch.");
        }

        LocalDate finalEnrolledDate = enrolledDate != null ? enrolledDate : LocalDate.now();

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Selected student not found."));

        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Selected batch not found."));

        return enrollmentRepository.findByStudentIdAndBatchId(studentId, batchId)
                .map(existingEnrollment -> {
                    if (existingEnrollment.getStatus() == EnrollmentStatus.ACTIVE) {
                        throw new IllegalStateException(
                                "This student is already enrolled in the selected batch."
                        );
                    }

                    existingEnrollment.setStatus(EnrollmentStatus.ACTIVE);
                    existingEnrollment.setEnrolledDate(finalEnrolledDate);
                    return enrollmentRepository.save(existingEnrollment);
                })
                .orElseGet(() -> {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setStudent(student);
                    enrollment.setBatch(batch);
                    enrollment.setEnrolledDate(finalEnrolledDate);
                    enrollment.setStatus(EnrollmentStatus.ACTIVE);

                    return enrollmentRepository.save(enrollment);
                });
    }

    @Transactional
    public void deactivateEnrollment(Long id) {
        Enrollment enrollment = getEnrollmentById(id);
        enrollment.setStatus(EnrollmentStatus.INACTIVE);
        enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void activateEnrollment(Long id) {
        Enrollment enrollment = getEnrollmentById(id);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void deleteEnrollment(Long id) {
        // Safer than hard delete because payments and attendance can be linked.
        deactivateEnrollment(id);
    }

    public List<Enrollment> getActiveEnrollments() {
        return enrollmentRepository.findByStatus(EnrollmentStatus.ACTIVE);
    }

    public List<Enrollment> getActiveEnrollmentsByBatchId(Long batchId) {
        return enrollmentRepository.findByBatchIdAndStatus(batchId, EnrollmentStatus.ACTIVE);
    }

    public Page<Enrollment> searchEnrollments(
            String keyword,
            EducationLevel educationLevel,
            Grade grade,
            StreamType stream,
            EnrollmentStatus status,
            Pageable pageable
    ) {
        return enrollmentRepository.searchEnrollments(
                keyword,
                educationLevel,
                grade,
                stream,
                status,
                pageable
        );
    }
}