package lk.janith.tuitionmanagement.entity;

import jakarta.persistence.*;
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

    @Column(name="batch_name", nullable = false)
    private String batchName;

    @Enumerated(EnumType.STRING)
    @Column(name="education_level", nullable = false)
    private EducationLevel educationLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Grade grade;

    @Enumerated(EnumType.STRING)
    private StreamType stream = StreamType.NONE;

    @Column(nullable = false)
    private String subject;

    @Column(name="teacher_name")
    private String teacherName;

    @Column(name="monthly_fee", nullable = false)
    private BigDecimal monthlyFee;

    @Enumerated(EnumType.STRING)
    @Column(name="day_of_week")
    private DayOfWeek dayOfWeek;

    @Column(name="start_time")
    private LocalTime startTime;

    @Column(name="end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private BatchStatus status = BatchStatus.ACTIVE;
}