package lk.janith.tuitionmanagement.repository;

import lk.janith.tuitionmanagement.entity.Batch;
import lk.janith.tuitionmanagement.enums.BatchStatus;
import lk.janith.tuitionmanagement.enums.EducationLevel;
import lk.janith.tuitionmanagement.enums.Grade;
import lk.janith.tuitionmanagement.enums.StreamType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BatchRepository extends JpaRepository<Batch, Long> {

    long countByStatus(BatchStatus status);

    @Query("""
            SELECT b FROM Batch b
            WHERE
            (
                :keyword IS NULL OR :keyword = '' OR
                LOWER(b.batchName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(b.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(b.teacherName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND (:educationLevel IS NULL OR b.educationLevel = :educationLevel)
            AND (:grade IS NULL OR b.grade = :grade)
            AND (:stream IS NULL OR b.stream = :stream)
            """)
    Page<Batch> searchBatches(
            @Param("keyword") String keyword,
            @Param("educationLevel") EducationLevel educationLevel,
            @Param("grade") Grade grade,
            @Param("stream") StreamType stream,
            Pageable pageable
    );
}