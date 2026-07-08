package lk.janith.tuitionmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @Column(name = "student_code", unique = true, nullable = false, length = 20)
    private String studentCode;

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot be longer than 100 characters")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^$|[0-9]{10}$", message = "Phone number must contain exactly 10 digits")
    @Column(name = "phone", nullable = false, length = 10)
    private String phone;

    @NotBlank(message = "Parent phone number is required")
    @Pattern(regexp = "^$|[0-9]{10}$", message = "Parent phone number must contain exactly 10 digits")
    @Column(name = "parent_phone", nullable = false, length = 10)
    private String parentPhone;

    @Size(max = 255, message = "Address cannot be longer than 255 characters")
    @Column(name = "address", length = 255)
    private String address;

    @NotBlank(message = "School name is required")
    @Size(max = 100, message = "School name cannot be longer than 100 characters")
    @Column(name = "school", nullable = false, length = 100)
    private String school;

    @NotNull(message = "Education level is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "education_level", nullable = false)
    private EducationLevel educationLevel;

    @NotNull(message = "Grade is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false)
    private Grade grade;

    @NotNull(message = "Stream is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "stream", nullable = false)
    private StreamType stream;

    @NotNull(message = "Joined date is required")
    @Column(name = "joined_date", nullable = false)
    private LocalDate joinedDate;

    @NotNull(message = "Student status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StudentStatus status;
}