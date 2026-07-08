package lk.janith.tuitionmanagement.controller;

import lk.janith.tuitionmanagement.entity.Batch;
import lk.janith.tuitionmanagement.enums.BatchStatus;
import lk.janith.tuitionmanagement.enums.EducationLevel;
import lk.janith.tuitionmanagement.enums.Grade;
import lk.janith.tuitionmanagement.enums.StreamType;
import lk.janith.tuitionmanagement.service.BatchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.time.DayOfWeek;

@Controller
@RequestMapping("/batches")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }
    
    @GetMapping
    public String listBatches(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String educationLevel,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String stream,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        EducationLevel selectedLevel = null;
        Grade selectedGrade = null;
        StreamType selectedStream = null;

        if (educationLevel != null && !educationLevel.isBlank()) {
            selectedLevel = EducationLevel.valueOf(educationLevel);
        }

        if (grade != null && !grade.isBlank()) {
            selectedGrade = Grade.valueOf(grade);
        }

        if (stream != null && !stream.isBlank()) {
            selectedStream = StreamType.valueOf(stream);
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<Batch> batchPage = batchService.searchBatches(
                keyword,
                selectedLevel,
                selectedGrade,
                selectedStream,
                pageable
        );

        int totalPages = batchPage.getTotalPages();

        int startPage = Math.max(0, page - 2);
        int endPage = Math.min(totalPages - 1, page + 2);

        if (totalPages > 0 && endPage - startPage < 4) {
            if (startPage == 0) {
                endPage = Math.min(totalPages - 1, startPage + 4);
            } else if (endPage == totalPages - 1) {
                startPage = Math.max(0, endPage - 4);
            }
        }

        model.addAttribute("batches", batchPage.getContent());
        model.addAttribute("batchPage", batchPage);

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedEducationLevel", educationLevel);
        model.addAttribute("selectedGrade", grade);
        model.addAttribute("selectedStream", stream);

        model.addAttribute("educationLevels", EducationLevel.values());
        model.addAttribute("grades", Grade.values());
        model.addAttribute("streams", StreamType.values());

        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", batchPage.getTotalElements());

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "batches/list";
    }


    @GetMapping("/new")
    public String showBatchForm(Model model) {
        model.addAttribute("batch", new Batch());
        addBatchFormData(model);

        return "batches/form";
    }


    // Save
    @PostMapping("/save")
    public String saveBatch(
            @Valid @ModelAttribute("batch") Batch batch,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            addBatchFormData(model);
            return "batches/form";
        }

        batchService.saveBatch(batch);

        return "redirect:/batches";
    }


    @GetMapping("/edit/{id}")
    public String editBatch(
            @PathVariable Long id,
            Model model
    ) {
        Batch batch = batchService.getBatchById(id);

        model.addAttribute("batch", batch);
        addBatchFormData(model);

        return "batches/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteBatch(@PathVariable Long id) {
        batchService.deleteBatch(id);

        return "redirect:/batches";
    }

    private void addBatchFormData(Model model) {
        model.addAttribute("educationLevels", EducationLevel.values());
        model.addAttribute("grades", Grade.values());
        model.addAttribute("streams", StreamType.values());

        model.addAttribute("statuses", BatchStatus.values());
        model.addAttribute("batchStatuses", BatchStatus.values());

        model.addAttribute("daysOfWeek", DayOfWeek.values());
        model.addAttribute("dayOfWeeks", DayOfWeek.values());
    }
}