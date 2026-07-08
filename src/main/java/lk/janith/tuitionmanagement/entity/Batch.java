package lk.janith.tuitionmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lk.janith.tuitionmanagement.enums.BatchStatus;
import lk.janith.tuitionmanagement.enums.EducationLevel;
import lk.janith.tuitionmanagement.enums.Grade;
import lk.janith.tuitionmanagement.enums.StreamType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "batches")
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Batch name is required")
    @Size(max = 150, message = "Batch name cannot be longer than 150 characters")
    @Column(name = "batch_name", nullable = false, length = 150)
    private String batchName;

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

    @NotBlank(message = "Subject is required")
    @Size(max = 100, message = "Subject cannot be longer than 100 characters")
    @Column(name = "subject", nullable = false, length = 100)
    private String subject;

    @NotBlank(message = "Teacher name is required")
    @Size(max = 100, message = "Teacher name cannot be longer than 100 characters")
    @Column(name = "teacher_name", nullable = false, length = 100)
    private String teacherName;

    @NotNull(message = "Monthly fee is required")
    @DecimalMin(value = "0.01", message = "Monthly fee must be greater than 0")
    @Column(name = "monthly_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyFee;

    @NotNull(message = "Class day is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @NotNull(message = "Batch status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BatchStatus status;
}