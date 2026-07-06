package lk.janith.tuitionmanagement.repository;

import lk.janith.tuitionmanagement.entity.Student;
import lk.janith.tuitionmanagement.enums.EducationLevel;
import lk.janith.tuitionmanagement.enums.Grade;
import lk.janith.tuitionmanagement.enums.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {

    long countByStatus(StudentStatus status);

    @Query("""
            SELECT s FROM Student s
            WHERE
            (
                :keyword IS NULL OR :keyword = '' OR
                LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(s.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(s.parentPhone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(s.school) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND (:educationLevel IS NULL OR s.educationLevel = :educationLevel)
            AND (:grade IS NULL OR s.grade = :grade)
            """)
    Page<Student> searchStudents(
            @Param("keyword") String keyword,
            @Param("educationLevel") EducationLevel educationLevel,
            @Param("grade") Grade grade,
            Pageable pageable
    );
}