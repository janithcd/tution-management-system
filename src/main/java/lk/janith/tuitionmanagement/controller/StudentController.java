package lk.janith.tuitionmanagement.controller;

import lk.janith.tuitionmanagement.entity.Student;
import lk.janith.tuitionmanagement.enums.EducationLevel;
import lk.janith.tuitionmanagement.enums.Grade;
import lk.janith.tuitionmanagement.enums.StreamType;
import lk.janith.tuitionmanagement.enums.StudentStatus;
import lk.janith.tuitionmanagement.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @GetMapping
    public String listStudents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String educationLevel,
            @RequestParam(required = false) String grade,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        EducationLevel selectedLevel = null;
        Grade selectedGrade = null;

        if (educationLevel != null && !educationLevel.isBlank()) {
            selectedLevel = EducationLevel.valueOf(educationLevel);
        }

        if (grade != null && !grade.isBlank()) {
            selectedGrade = Grade.valueOf(grade);
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<Student> studentPage = studentService.searchStudents(
                keyword,
                selectedLevel,
                selectedGrade,
                pageable
        );

        int totalPages = studentPage.getTotalPages();

        int startPage = Math.max(0, page - 2);
        int endPage = Math.min(totalPages - 1, page + 2);

        if (totalPages > 0 && endPage - startPage < 4) {
            if (startPage == 0) {
                endPage = Math.min(totalPages - 1, startPage + 4);
            } else if (endPage == totalPages - 1) {
                startPage = Math.max(0, endPage - 4);
            }
        }

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("studentPage", studentPage);

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedEducationLevel", educationLevel);
        model.addAttribute("selectedGrade", grade);

        model.addAttribute("educationLevels", EducationLevel.values());
        model.addAttribute("grades", Grade.values());

        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", studentPage.getTotalElements());

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "students/list";
    }


    @GetMapping("/new")
    public String showStudentForm(Model model) {
        model.addAttribute("student", new Student());
        addStudentFormData(model);

        return "students/form";
    }


    // Save
    @PostMapping("/save")
    public String saveStudent(
            @Valid @ModelAttribute("student") Student student,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            addStudentFormData(model);
            return "students/form";
        }

        studentService.saveStudent(student);

        return "redirect:/students";
    }


    @GetMapping("/edit/{id}")
    public String editStudent(
            @PathVariable Long id,
            Model model
    ) {
        Student student = studentService.getStudentById(id);

        model.addAttribute("student", student);
        addStudentFormData(model);

        return "students/form";
    }


    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);

        return "redirect:/students";
    }

    private void addStudentFormData(Model model) {
        model.addAttribute("educationLevels", EducationLevel.values());
        model.addAttribute("grades", Grade.values());
        model.addAttribute("streams", StreamType.values());
        model.addAttribute("statuses", StudentStatus.values());
    }
}