package student_registration.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;

import student_registration.entity.ActivityLog;
import student_registration.entity.Student;
import student_registration.repository.ActivityLogRepository;
import student_registration.repository.StudentRepository;

@Controller
public class StudentController {

    private final StudentRepository studentRepository;
    private final ActivityLogRepository activityLogRepository;

    public StudentController(
            StudentRepository studentRepository,
            ActivityLogRepository activityLogRepository) {

        this.studentRepository = studentRepository;
        this.activityLogRepository = activityLogRepository;
    }

    @GetMapping("/")
    public String showRegistrationForm(Model model) {
        model.addAttribute("student", new Student());
        return "registration";
    }

    // Register Student

    @PostMapping("/register")
    public String registerStudent(Student student) {

        studentRepository.save(student);

        ActivityLog log = new ActivityLog(
                "REGISTERED",
                student.getName(),
                LocalDateTime.now()
        );

        activityLogRepository.save(log);

        return "redirect:/students";
    }

    // View Students

    @GetMapping("/students")
    public String showStudents(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        return "students";
    }

    // Search Students

    @GetMapping("/search")
    public String searchStudents(
            @RequestParam("keyword") String keyword,
            Model model) {

        if (keyword == null || keyword.trim().isEmpty()) {

            model.addAttribute(
                    "students",
                    studentRepository.findAll()
            );

        } else {

            model.addAttribute(
                    "students",
                    studentRepository.findByNameContainingIgnoreCase(keyword)
            );
        }

        model.addAttribute("keyword", keyword);

        return "students";
    }

    // Delete Student

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable int id) {

        Student student =
                studentRepository.findById(id).orElse(null);

        if (student != null) {

            ActivityLog log = new ActivityLog(
                    "DELETED",
                    student.getName(),
                    LocalDateTime.now()
            );

            activityLogRepository.save(log);

            studentRepository.deleteById(id);
        }

        return "redirect:/students";
    }

    // Edit Student

    @GetMapping("/edit/{id}")
    public String editStudent(
            @PathVariable int id,
            Model model) {

        Student student =
                studentRepository.findById(id).orElse(null);

        model.addAttribute("student", student);

        return "edit";
    }

    // Update Student

    @PostMapping("/update")
    public String updateStudent(Student student) {

        studentRepository.save(student);

        ActivityLog log = new ActivityLog(
                "UPDATED",
                student.getName(),
                LocalDateTime.now()
        );

        activityLogRepository.save(log);

        return "redirect:/students";
    }

    // Dashboard

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        long totalStudents = studentRepository.count();

        long cseStudents =
                studentRepository
                        .findByDepartmentContainingIgnoreCase("CSE")
                        .size();

        long aiMlStudents =
                studentRepository
                        .findByDepartmentContainingIgnoreCase("CSE-AI & ML")
                        .size();

        long eceStudents =
                studentRepository
                        .findByDepartmentContainingIgnoreCase("ECE")
                        .size();

        long eeeStudents =
                studentRepository
                        .findByDepartmentContainingIgnoreCase("EEE")
                        .size();

        long mechStudents =
                studentRepository
                        .findByDepartmentContainingIgnoreCase("MECH")
                        .size();

        long civilStudents =
                studentRepository
                        .findByDepartmentContainingIgnoreCase("CIVIL")
                        .size();

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("cseStudents", cseStudents);
        model.addAttribute("aiMlStudents", aiMlStudents);
        model.addAttribute("eceStudents", eceStudents);
        model.addAttribute("eeeStudents", eeeStudents);
        model.addAttribute("mechStudents", mechStudents);
        model.addAttribute("civilStudents", civilStudents);

        return "dashboard";
    }

    // Activity Log

    @GetMapping("/activity")
    public String showActivityLog(Model model) {

        model.addAttribute(
                "logs",
                activityLogRepository.findTop10ByOrderByTimestampDesc()
        );

        return "activity";
    }

    // Export Students to CSV

    @GetMapping("/export")
    public void exportStudents(
            HttpServletResponse response) throws IOException {

        response.setContentType("text/csv");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=students.csv"
        );

        PrintWriter writer = response.getWriter();

        writer.println("ID,Name,Email,Department,Phone");

        for (Student student : studentRepository.findAll()) {

            writer.println(
                    student.getId() + "," +
                    student.getName() + "," +
                    student.getEmail() + "," +
                    student.getDepartment() + "," +
                    student.getPhone()
            );
        }

        writer.flush();
        writer.close();
    }
}

