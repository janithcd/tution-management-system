package lk.janith.tuitionmanagement.controller;

import lk.janith.tuitionmanagement.entity.Student;
import lk.janith.tuitionmanagement.enums.EducationLevel;
import lk.janith.tuitionmanagement.enums.Grade;
import lk.janith.tuitionmanagement.enums.StreamType;
import lk.janith.tuitionmanagement.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
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