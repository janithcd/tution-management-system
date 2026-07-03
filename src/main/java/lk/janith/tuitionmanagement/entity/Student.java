package lk.janith.tuitionmanagement.entity;

import jakarta.persistence.*;
import lk.janith.tuitionmanagement.enums.EducationLevel;
import lk.janith.tuitionmanagement.enums.Grade;
import lk.janith.tuitionmanagement.enums.StreamType;
import lk.janith.tuitionmanagement.enums.StudentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="student_code", unique = true, nullable = false)
    private String studentCode;

    @Column(name="full_name", nullable = false)
    private String fullName;

    private String phone;

    @Column(name="parent_phone")
    private String parentPhone;

    private String address;

    private String school;

    @Enumerated(EnumType.STRING)
    @Column(name="education_level", nullable = false)
    private EducationLevel educationLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Grade grade;

    @Enumerated(EnumType.STRING)
    private StreamType stream = StreamType.NONE;

    @Column(name="joined_date")
    private LocalDate joinedDate;

    @Enumerated(EnumType.STRING)
    private StudentStatus status = StudentStatus.ACTIVE;
}