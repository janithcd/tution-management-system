package lk.janith.tuitionmanagement.repository;

import lk.janith.tuitionmanagement.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<Batch, Long> {
}