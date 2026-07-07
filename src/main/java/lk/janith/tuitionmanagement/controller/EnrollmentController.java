package lk.janith.tuitionmanagement.controller;

import lk.janith.tuitionmanagement.service.BatchService;
import lk.janith.tuitionmanagement.service.EnrollmentService;
import lk.janith.tuitionmanagement.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lk.janith.tuitionmanagement.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import lk.janith.tuitionmanagement.entity.Enrollment;
import lk.janith.tuitionmanagement.enums.EducationLevel;
import lk.janith.tuitionmanagement.enums.EnrollmentStatus;
import lk.janith.tuitionmanagement.enums.Grade;
import lk.janith.tuitionmanagement.enums.StreamType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final BatchService batchService;

    public EnrollmentController(
            EnrollmentService enrollmentService,
            StudentService studentService,
            BatchService batchService
    ) {
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.batchService = batchService;
    }

    @GetMapping
    public String listEnrollments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String educationLevel,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String stream,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        EducationLevel selectedLevel = null;
        Grade selectedGrade = null;
        StreamType selectedStream = null;
        EnrollmentStatus selectedStatus = null;

        if (educationLevel != null && !educationLevel.isBlank()) {
            selectedLevel = EducationLevel.valueOf(educationLevel);
        }

        if (grade != null && !grade.isBlank()) {
            selectedGrade = Grade.valueOf(grade);
        }

        if (stream != null && !stream.isBlank()) {
            selectedStream = StreamType.valueOf(stream);
        }

        if (status != null && !status.isBlank()) {
            selectedStatus = EnrollmentStatus.valueOf(status);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Enrollment> enrollmentPage = enrollmentService.searchEnrollments(
                keyword,
                selectedLevel,
                selectedGrade,
                selectedStream,
                selectedStatus,
                pageable
        );

        model.addAttribute("enrollments", enrollmentPage.getContent());
        model.addAttribute("enrollmentPage", enrollmentPage);

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedEducationLevel", educationLevel);
        model.addAttribute("selectedGrade", grade);
        model.addAttribute("selectedStream", stream);
        model.addAttribute("selectedStatus", status);

        model.addAttribute("educationLevels", EducationLevel.values());
        model.addAttribute("grades", Grade.values());
        model.addAttribute("streams", StreamType.values());
        model.addAttribute("statuses", EnrollmentStatus.values());

        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", enrollmentPage.getTotalPages());
        model.addAttribute("totalItems", enrollmentPage.getTotalElements());

        return "enrollments/list";
    }

    @GetMapping("/new")
    public String showEnrollmentForm(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("batches", batchService.getAllBatches());
        return "enrollments/form";
    }

    @PostMapping("/save")
    public String saveEnrollment(
            @RequestParam Long studentId,
            @RequestParam Long batchId,
            Model model
    ) {
        try {
            enrollmentService.enrollStudent(studentId, batchId);
            return "redirect:/enrollments";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("batches", batchService.getAllBatches());
            return "enrollments/form";
        }
    }

    @GetMapping("/deactivate/{id}")
    public String deactivateEnrollment(@PathVariable Long id) {
        enrollmentService.deactivateEnrollment(id);
        return "redirect:/enrollments";
    }

    @GetMapping("/activate/{id}")
    public String activateEnrollment(@PathVariable Long id) {
        enrollmentService.activateEnrollment(id);
        return "redirect:/enrollments";
    }

    @GetMapping("/delete/{id}")
    public String deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return "redirect:/enrollments";
    }
}