package lk.janith.tuitionmanagement.controller;

import lk.janith.tuitionmanagement.entity.Enrollment;
import lk.janith.tuitionmanagement.enums.EducationLevel;
import lk.janith.tuitionmanagement.enums.EnrollmentStatus;
import lk.janith.tuitionmanagement.enums.Grade;
import lk.janith.tuitionmanagement.enums.StreamType;
import lk.janith.tuitionmanagement.service.BatchService;
import lk.janith.tuitionmanagement.service.EnrollmentService;
import lk.janith.tuitionmanagement.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

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
        EducationLevel selectedLevel = parseEducationLevel(educationLevel);
        Grade selectedGrade = parseGrade(grade);
        StreamType selectedStream = parseStream(stream);
        EnrollmentStatus selectedStatus = parseStatus(status);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<Enrollment> enrollmentPage = enrollmentService.searchEnrollments(
                keyword,
                selectedLevel,
                selectedGrade,
                selectedStream,
                selectedStatus,
                pageable
        );

        int totalPages = enrollmentPage.getTotalPages();

        int startPage = Math.max(0, page - 2);
        int endPage = Math.min(totalPages - 1, page + 2);

        if (totalPages > 0 && endPage - startPage < 4) {
            if (startPage == 0) {
                endPage = Math.min(totalPages - 1, startPage + 4);
            } else if (endPage == totalPages - 1) {
                startPage = Math.max(0, endPage - 4);
            }
        }

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
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", enrollmentPage.getTotalElements());

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "enrollments/list";
    }

    @GetMapping("/new")
    public String showEnrollmentForm(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("batches", batchService.getAllBatches());
        model.addAttribute("today", LocalDate.now());

        return "enrollments/form";
    }

    @PostMapping("/save")
    public String saveEnrollment(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long batchId,
            @RequestParam(required = false) LocalDate enrolledDate,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Enrollment enrollment = enrollmentService.enrollStudent(studentId, batchId, enrolledDate);

            if (enrollment.getStatus() == EnrollmentStatus.ACTIVE) {
                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "Student enrolled successfully."
                );
            }

        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Enrollment could not be saved. Please check the selected student and batch."
            );
        }

        return "redirect:/enrollments";
    }

    @GetMapping("/activate/{id}")
    public String activateEnrollment(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            enrollmentService.activateEnrollment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Enrollment activated successfully.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Enrollment could not be activated.");
        }

        return "redirect:/enrollments";
    }

    @GetMapping("/deactivate/{id}")
    public String deactivateEnrollment(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            enrollmentService.deactivateEnrollment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Enrollment deactivated successfully.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Enrollment could not be deactivated.");
        }

        return "redirect:/enrollments";
    }

    @GetMapping("/delete/{id}")
    public String deleteEnrollment(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            enrollmentService.deleteEnrollment(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Enrollment deactivated successfully. It was not permanently deleted because related records may exist."
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Enrollment could not be deleted.");
        }

        return "redirect:/enrollments";
    }

    private EducationLevel parseEducationLevel(String value) {
        try {
            return value == null || value.isBlank() ? null : EducationLevel.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

    private Grade parseGrade(String value) {
        try {
            return value == null || value.isBlank() ? null : Grade.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

    private StreamType parseStream(String value) {
        try {
            return value == null || value.isBlank() ? null : StreamType.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

    private EnrollmentStatus parseStatus(String value) {
        try {
            return value == null || value.isBlank() ? null : EnrollmentStatus.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }
}