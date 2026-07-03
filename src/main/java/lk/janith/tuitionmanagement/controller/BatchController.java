package lk.janith.tuitionmanagement.controller;

import lk.janith.tuitionmanagement.entity.Batch;
import lk.janith.tuitionmanagement.enums.BatchStatus;
import lk.janith.tuitionmanagement.enums.EducationLevel;
import lk.janith.tuitionmanagement.enums.Grade;
import lk.janith.tuitionmanagement.enums.StreamType;
import lk.janith.tuitionmanagement.service.BatchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;

@Controller
@RequestMapping("/batches")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @GetMapping
    public String listBatches(Model model) {
        model.addAttribute("batches", batchService.getAllBatches());
        return "batches/list";
    }

    @GetMapping("/new")
    public String showAddBatchForm(Model model) {
        model.addAttribute("batch", new Batch());
        loadFormData(model);
        return "batches/form";
    }

    @PostMapping("/save")
    public String saveBatch(@ModelAttribute Batch batch) {
        batchService.saveBatch(batch);
        return "redirect:/batches";
    }

    @GetMapping("/edit/{id}")
    public String showEditBatchForm(@PathVariable Long id, Model model) {
        model.addAttribute("batch", batchService.getBatchById(id));
        loadFormData(model);
        return "batches/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteBatch(@PathVariable Long id) {
        batchService.deleteBatch(id);
        return "redirect:/batches";
    }

    private void loadFormData(Model model) {
        model.addAttribute("educationLevels", EducationLevel.values());
        model.addAttribute("grades", Grade.values());
        model.addAttribute("streams", StreamType.values());
        model.addAttribute("days", DayOfWeek.values());
        model.addAttribute("statuses", BatchStatus.values());
    }
}