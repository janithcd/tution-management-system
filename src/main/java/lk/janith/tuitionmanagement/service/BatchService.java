package lk.janith.tuitionmanagement.service;

import lk.janith.tuitionmanagement.entity.Batch;
import lk.janith.tuitionmanagement.repository.BatchRepository;
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
}