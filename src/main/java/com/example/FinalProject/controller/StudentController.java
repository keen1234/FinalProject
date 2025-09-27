package com.example.FinalProject.controller;

import com.example.FinalProject.model.Student;
import com.example.FinalProject.model.Course;
import com.example.FinalProject.repository.CourseRepository;
import com.example.FinalProject.repository.StudentRepository;
import com.example.FinalProject.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.util.List;


@Controller
public class StudentController {
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("course");
    }

    @PostMapping("/user-signup")
    public String signup(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("address") String address,
            @RequestParam("number") String number,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("course") String courseCode) {

        // Check if email already exists
        if (studentRepository.findByEmail(email).isPresent()) {
            return "redirect:/user-signup?error=email";
        }

        // Find the Course entity by courseCode
        Course course = courseRepository.findByCourseCode(courseCode);
        if (course == null) {
            // Handle error (course not found)
            return "redirect:/user-signup?error=course";
        }

        String encodedPassword = passwordEncoder.encode(password);
        logger.info("Encoded password for {}: {}", email, encodedPassword);
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setAddress(address);
        student.setNumber(number);
        student.setEmail(email);
        student.setPassword(encodedPassword);
        student.setCourse(course);  // <-- This sets the foreign key course_id

        studentRepository.save(student);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        Object adminObj = session.getAttribute("admin");
        if (adminObj instanceof com.example.FinalProject.model.admin admin) {
            admin.setStatus("offline");
            // Save the updated status to the database
            com.example.FinalProject.service.AdminService adminService = (com.example.FinalProject.service.AdminService) org.springframework.web.context.ContextLoaderListener.getCurrentWebApplicationContext().getBean(com.example.FinalProject.service.AdminService.class);
            adminService.save(admin);
        }
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/userprofile")
    public String userProfile(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof com.example.FinalProject.model.StudentDetails userDetails) {
            Student student = userDetails.getStudent();
            model.addAttribute("student", student);
        }
        return "userprofile";
    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof com.example.FinalProject.model.StudentDetails userDetails) {
                Student student = userDetails.getStudent();
                model.addAttribute("student", student);
            }
        }
        return "home";
    }

}
