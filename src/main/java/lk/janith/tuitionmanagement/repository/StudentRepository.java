package lk.janith.tuitionmanagement.repository;

import lk.janith.tuitionmanagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}