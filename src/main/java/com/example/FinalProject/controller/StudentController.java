package com.example.FinalProject.controller;

import com.example.FinalProject.model.Student;
import com.example.FinalProject.model.Course;
import com.example.FinalProject.model.Notification;
import com.example.FinalProject.repository.CourseRepository;
import com.example.FinalProject.repository.StudentRepository;
import com.example.FinalProject.repository.NotificationRepository;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

import java.util.List;
import java.util.ArrayList;

// New imports
import com.example.FinalProject.repository.BorrowRecordRepository;
import com.example.FinalProject.repository.ReservationRepository;
import com.example.FinalProject.model.BorrowRecord;
import com.example.FinalProject.model.Reservation;

@Controller
public class StudentController {
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private NotificationRepository notificationRepository;

    // Autowire new repositories
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private ReservationRepository reservationRepository;

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

                // Pass BorrowRecord list directly so Thymeleaf can access br.book and br.dueDate
                try {
                    List<BorrowRecord> borrowRecords = borrowRecordRepository.findDueByStudentId(student.getId());
                    model.addAttribute("dueBooks", borrowRecords != null ? borrowRecords : new ArrayList<BorrowRecord>());
                } catch (Exception e) {
                    logger.error("Error loading borrow records for student {}: {}", student.getId(), e.getMessage());
                    model.addAttribute("dueBooks", new ArrayList<BorrowRecord>());
                }

                // Pass Reservation list directly so Thymeleaf can access r.book and r.createdAt
                try {
                    List<Reservation> reservedList = new ArrayList<>();
                    List<Reservation> pending = reservationRepository.findByStudentAndStatus(student, Reservation.Status.pending);
                    if (pending != null) reservedList.addAll(pending);
                    List<Reservation> accepted = reservationRepository.findByStudentAndStatus(student, Reservation.Status.accepted);
                    if (accepted != null) reservedList.addAll(accepted);
                    model.addAttribute("reservedBooks", reservedList);
                } catch (Exception e) {
                    logger.error("Error loading reservations for student {}: {}", student.getId(), e.getMessage());
                    model.addAttribute("reservedBooks", new ArrayList<Reservation>());
                }
            }
        }
        return "home";
    }
    
    @GetMapping("/student/notifications")
    public String viewNotifications(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof com.example.FinalProject.model.StudentDetails userDetails) {
            Student student = userDetails.getStudent();
            model.addAttribute("student", student);
            
            // Get all notifications for this student, ordered by creation date (newest first)
            List<Notification> notifications = notificationRepository.findByStudentOrderByCreatedAtDesc(student);
            model.addAttribute("notifications", notifications);
            
            // Mark all notifications as read when viewed
            for (Notification notification : notifications) {
                if (!notification.isRead()) {
                    notification.setRead(true);
                    notificationRepository.save(notification);
                }
            }
        }
        
        return "student-notifications";
    }
    
    @GetMapping("/api/student/notifications/count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getNotificationCount(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("count", 0);
            response.put("notifications", new ArrayList<>());
            return ResponseEntity.ok(response);
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof com.example.FinalProject.model.StudentDetails userDetails) {
            Student student = userDetails.getStudent();
            
            // Get unread notifications
            List<Notification> unreadNotifications = notificationRepository.findByStudentAndIsReadFalse(student);
            List<Map<String, Object>> notificationList = new ArrayList<>();
            
            for (Notification notification : unreadNotifications) {
                Map<String, Object> notifMap = new HashMap<>();
                notifMap.put("id", notification.getId());
                notifMap.put("message", notification.getMessage());
                notifMap.put("createdAt", notification.getCreatedAt().toString());
                notificationList.add(notifMap);
            }
            
            response.put("count", unreadNotifications.size());
            response.put("notifications", notificationList);
        } else {
            response.put("count", 0);
            response.put("notifications", new ArrayList<>());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/api/student/notifications/mark-read")
    @ResponseBody
    public ResponseEntity<Map<String, String>> markNotificationsAsRead(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("status", "error");
            response.put("message", "Not authenticated");
            return ResponseEntity.status(401).body(response);
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof com.example.FinalProject.model.StudentDetails userDetails) {
            Student student = userDetails.getStudent();
            
            // Mark all unread notifications as read
            List<Notification> unreadNotifications = notificationRepository.findByStudentAndIsReadFalse(student);
            for (Notification notification : unreadNotifications) {
                notification.setRead(true);
                notificationRepository.save(notification);
            }
            
            response.put("status", "success");
            response.put("message", "Notifications marked as read");
        } else {
            response.put("status", "error");
            response.put("message", "Student not found");
        }
        
        return ResponseEntity.ok(response);
    }

}
