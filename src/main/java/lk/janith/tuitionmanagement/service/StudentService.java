package lk.janith.tuitionmanagement.service;

import lk.janith.tuitionmanagement.entity.Student;
import lk.janith.tuitionmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;
import lk.janith.tuitionmanagement.enums.EducationLevel;
import lk.janith.tuitionmanagement.enums.Grade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student saveStudent(Student student) {
        if (student.getJoinedDate() == null) {
            student.setJoinedDate(LocalDate.now());
        }

        if (student.getStudentCode() == null || student.getStudentCode().isBlank()) {
            student.setStudentCode(generateStudentCode());
        }

        return studentRepository.save(student);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    private String generateStudentCode() {
        long count = studentRepository.count() + 1;
        return String.format("STU%04d", count);
    }
    public Page<Student> searchStudents(
            String keyword,
            EducationLevel educationLevel,
            Grade grade,
            Pageable pageable
    ) {
        return studentRepository.searchStudents(keyword, educationLevel, grade, pageable);
    }
}