package lk.janith.tuitionmanagement.controller;

import lk.janith.tuitionmanagement.entity.Student;
import lk.janith.tuitionmanagement.enums.EducationLevel;
import lk.janith.tuitionmanagement.enums.Grade;
import lk.janith.tuitionmanagement.enums.StreamType;
import lk.janith.tuitionmanagement.service.StudentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


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

        Pageable pageable = PageRequest.of(page, size);

        Page<Student> studentPage = studentService.searchStudents(
                keyword,
                selectedLevel,
                selectedGrade,
                pageable
        );

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("studentPage", studentPage);

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedEducationLevel", educationLevel);
        model.addAttribute("selectedGrade", grade);

        model.addAttribute("educationLevels", EducationLevel.values());
        model.addAttribute("grades", Grade.values());

        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("totalItems", studentPage.getTotalElements());

        return "students/list";
    }

    @GetMapping("/new")
    public String showAddStudentForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("educationLevels", EducationLevel.values());
        model.addAttribute("grades", Grade.values());
        model.addAttribute("streams", StreamType.values());
        return "students/form";
    }

    @PostMapping("/save")
    public String saveStudent(@ModelAttribute Student student) {
        studentService.saveStudent(student);
        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String showEditStudentForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        model.addAttribute("educationLevels", EducationLevel.values());
        model.addAttribute("grades", Grade.values());
        model.addAttribute("streams", StreamType.values());
        return "students/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }
}