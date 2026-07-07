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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Enrollment> enrollmentPage = enrollmentService.getEnrollmentPage(pageable);

        model.addAttribute("enrollments", enrollmentPage.getContent());
        model.addAttribute("enrollmentPage", enrollmentPage);

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