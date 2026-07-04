package lk.janith.tuitionmanagement.service;

import lk.janith.tuitionmanagement.entity.Batch;
import lk.janith.tuitionmanagement.entity.Enrollment;
import lk.janith.tuitionmanagement.entity.Student;
import lk.janith.tuitionmanagement.enums.EnrollmentStatus;
import lk.janith.tuitionmanagement.repository.BatchRepository;
import lk.janith.tuitionmanagement.repository.EnrollmentRepository;
import lk.janith.tuitionmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;

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

    public List<Enrollment> getActiveEnrollmentsByBatchId(Long batchId) {
        return enrollmentRepository.findByBatchIdAndStatus(batchId, EnrollmentStatus.ACTIVE);
    }

    public List<Enrollment> getActiveEnrollments() {
        return enrollmentRepository.findByStatus(EnrollmentStatus.ACTIVE);
    }

    public Enrollment enrollStudent(Long studentId, Long batchId) {

        enrollmentRepository.findByStudentIdAndBatchId(studentId, batchId)
                .ifPresent(existing -> {
                    throw new RuntimeException("Student is already enrolled in this batch");
                });

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setBatch(batch);
        enrollment.setEnrolledDate(LocalDate.now());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        return enrollmentRepository.save(enrollment);
    }

    public Enrollment getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
    }

    public void deactivateEnrollment(Long id) {
        Enrollment enrollment = getEnrollmentById(id);
        enrollment.setStatus(EnrollmentStatus.INACTIVE);
        enrollmentRepository.save(enrollment);
    }

    public void activateEnrollment(Long id) {
        Enrollment enrollment = getEnrollmentById(id);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollmentRepository.save(enrollment);
    }

    public void deleteEnrollment(Long id) {
        enrollmentRepository.deleteById(id);
    }
}