package lk.janith.tuitionmanagement.service;

import lk.janith.tuitionmanagement.entity.Batch;
import lk.janith.tuitionmanagement.enums.EducationLevel;
import lk.janith.tuitionmanagement.enums.Grade;
import lk.janith.tuitionmanagement.enums.StreamType;
import lk.janith.tuitionmanagement.repository.BatchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchService {

    private final BatchRepository batchRepository;

    public BatchService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    public Batch saveBatch(Batch batch) {
        return batchRepository.save(batch);
    }

    public Batch getBatchById(Long id) {
        return batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
    }

    public void deleteBatch(Long id) {
        batchRepository.deleteById(id);
    }

    public Page<Batch> searchBatches(
            String keyword,
            EducationLevel educationLevel,
            Grade grade,
            StreamType stream,
            Pageable pageable
    ) {
        return batchRepository.searchBatches(keyword, educationLevel, grade, stream, pageable);
    }
}