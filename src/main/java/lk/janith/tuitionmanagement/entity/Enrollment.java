package lk.janith.tuitionmanagement.entity;

import jakarta.persistence.*;
import lk.janith.tuitionmanagement.enums.EnrollmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "batch_id"})
        }
)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @Column(name = "enrolled_date")
    private LocalDate enrolledDate;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;
}