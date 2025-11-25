package com.example.FinalProject.controller;

import com.example.FinalProject.dto.StudentProfileDto;
import com.example.FinalProject.model.Student;
import com.example.FinalProject.model.Course;
import com.example.FinalProject.model.Notification;
import com.example.FinalProject.repository.CourseRepository;
import com.example.FinalProject.repository.StudentRepository;
import com.example.FinalProject.repository.NotificationRepository;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.HashMap;
import java.util.Map;

import java.util.List;
import java.util.ArrayList;

// New imports
import com.example.FinalProject.repository.BorrowRecordRepository;
import com.example.FinalProject.repository.ReservationRepository;
import com.example.FinalProject.model.BorrowRecord;
import com.example.FinalProject.model.Reservation;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

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

    @Autowired
    private StudentService studentService;

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
            // Load fresh student from repository to ensure view shows persisted changes
            String email = userDetails.getUsername();
            studentRepository.findByEmail(email).ifPresent(s -> model.addAttribute("student", s));
        }
        return "userprofile";
    }

    @GetMapping("/userprofile/edit")
    public String editUserProfile(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof com.example.FinalProject.model.StudentDetails userDetails) {
            Student student = userDetails.getStudent();
            StudentProfileDto dto = new StudentProfileDto();
            dto.setId(student.getId());
            dto.setFirstName(student.getFirstName());
            dto.setLastName(student.getLastName());
            dto.setAddress(student.getAddress());
            dto.setNumber(student.getNumber());
            dto.setEmail(student.getEmail());
            if (student.getCourse() != null) dto.setCourseCode(student.getCourse().getCourseCode());

            model.addAttribute("profileDto", dto);
            model.addAttribute("courses", courseRepository.findAll());
            return "edit-userprofile";
        }
        return "redirect:/login";
    }

    @PostMapping("/userprofile/edit")
    public String postEditUserProfile(Authentication authentication, @Valid @ModelAttribute("profileDto") StudentProfileDto profileDto, BindingResult br, Model model, jakarta.servlet.http.HttpSession session) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof com.example.FinalProject.model.StudentDetails userDetails)) {
            return "redirect:/login";
        }

        Student current = userDetails.getStudent();

        if (br.hasErrors()) {
            model.addAttribute("courses", courseRepository.findAll());
            return "edit-userprofile";
        }

        // ensure user cannot edit someone else's account
        if (!current.getId().equals(profileDto.getId())) {
            return "redirect:/userprofile?error=forbidden";
        }

        boolean credentialsChanged = false;
        try {
            // log incoming DTO for debugging
            logger.info("Received profile update DTO for student id={}: firstName={}, lastName={}, email={}, courseCode={}", profileDto.getId(), profileDto.getFirstName(), profileDto.getLastName(), profileDto.getEmail(), profileDto.getCourseCode());

            // check if email or password will change
            if (!current.getEmail().equals(profileDto.getEmail())) credentialsChanged = true;
            if (profileDto.getPassword() != null && !profileDto.getPassword().isBlank()) credentialsChanged = true;

            // update and get the persisted student
            Student updated = studentService.updateProfile(current.getId(), profileDto);
            // if credentials not changed, update authentication principal so UI shows new data immediately
            if (!credentialsChanged) {
                com.example.FinalProject.model.StudentDetails newDetails = new com.example.FinalProject.model.StudentDetails(updated);
                UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(newDetails, authentication.getCredentials(), authentication.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(newAuth);

                // redirect to profile page so user returns to their profile and sees the updated info
                return "redirect:/userprofile?success";
            }
        } catch (IllegalArgumentException ex) {
            br.reject("globalError", ex.getMessage());
            model.addAttribute("courses", courseRepository.findAll());
            return "edit-userprofile";
        }

        if (credentialsChanged) {
            // invalidate session so user must re-login when credentials changed
            try { session.invalidate(); } catch (Exception ignored) {}
            return "redirect:/login?updated";
        }

        return "redirect:/userprofile?success";
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
