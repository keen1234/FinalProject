package com.example.FinalProject.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.FinalProject.service.AdminDetails;
import com.example.FinalProject.service.AdminService;
import com.example.FinalProject.model.Notification;
import com.example.FinalProject.repository.NotificationRepository;
import org.springframework.security.core.context.SecurityContextImpl;

import com.example.FinalProject.repository.BorrowRecordRepository;
import com.example.FinalProject.repository.ReservationRepository;
import com.example.FinalProject.model.BorrowRecord;
import com.example.FinalProject.model.Reservation;
import com.example.FinalProject.model.StudentDetails;
import com.example.FinalProject.model.Book;
import com.example.FinalProject.repository.AdminRepository;
import com.example.FinalProject.model.admin;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    @Autowired
    private AdminService adminService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof StudentDetails) {
            StudentDetails userDetails = (StudentDetails) authentication.getPrincipal();
            model.addAttribute("student", userDetails.getStudent());

            // Add unread notification count for initial display
            List<Notification> unreadNotifications = notificationRepository.findByStudentAndIsReadFalse(userDetails.getStudent());
            model.addAttribute("unreadNotificationCount", unreadNotifications.size());

            // Fetch due books and reserved books for the current student and add to model
            try {
                Long studentId = userDetails.getStudent().getId();
                List<BorrowRecord> dueBooks = borrowRecordRepository.findDueByStudentId(studentId);
                List<Reservation> reservedReservations = reservationRepository.findByStudentIdAndStatusIn(
                    studentId,
                    java.util.List.of(Reservation.Status.pending, Reservation.Status.accepted)
                );
                // Keep reservations as Reservation objects so template can access r.book and r.createdAt
                model.addAttribute("dueBooks", dueBooks);
                model.addAttribute("reservedBooks", reservedReservations);
            } catch (Exception e) {
                model.addAttribute("dueBooks", List.of());
                model.addAttribute("reservedBooks", List.of());
            }
        } else {
            model.addAttribute("dueBooks", List.of());
            model.addAttribute("reservedBooks", List.of());
        }
        return "home";
    }
    @GetMapping("/about")
    public String about(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof StudentDetails) {
            StudentDetails userDetails = (StudentDetails) authentication.getPrincipal();
            model.addAttribute("student", userDetails.getStudent());

            // Add unread notification count for initial display
            List<Notification> unreadNotifications = notificationRepository.findByStudentAndIsReadFalse(userDetails.getStudent());
            model.addAttribute("unreadNotificationCount", unreadNotifications.size());
        }
        return "about";
    }
    @GetMapping("/user-signup")
    public String showSignupPage() {
        return "user-signup";
    }
    @GetMapping("/admin-book")
    public String adminBookPage() {
        // Ensure we always hit the controller that populates the books list
        return "redirect:/admin/book";
    }
    @GetMapping("/admin-home")
    public String adminHomePage(Authentication authentication, Model model, HttpSession session) {
        logger.debug("/admin-home called");
        if (authentication != null) {
            logger.debug("Authentication isAuthenticated: {}", authentication.isAuthenticated());
            Object authPrincipal = authentication.getPrincipal();
            logger.debug("Principal type: {}", authPrincipal != null ? authPrincipal.getClass().getName() : "null");
            logger.debug("Authorities: {}", authentication.getAuthorities());
        } else {
            logger.debug("Authentication is null");
        }
        logger.debug("Session SPRING_SECURITY_CONTEXT: {}", session.getAttribute("SPRING_SECURITY_CONTEXT"));
        logger.debug("Authentication in controller: {}", authentication);
        admin adminObj = null;
        if (authentication != null && authentication.isAuthenticated() && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof AdminDetails) {
                adminObj = ((AdminDetails) principal).getAdmin();
            } else if (principal instanceof admin) {
                adminObj = (admin) principal;
            }
            model.addAttribute("admin", adminObj);
            return "admin-home";
        } else {
            logger.debug("Redirecting to /login");
            return "redirect:/login";
        }
    }
    @PostMapping("/admin-login")
    public String adminLogin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        admin adminObj = adminService.findByEmailAndPassword(email, password);
        if (adminObj != null) {
            // Mark admin as online and persist
            adminObj.setStatus("online");
            adminService.save(adminObj);
            session.setAttribute("admin", adminObj);
             // Set authentication in Spring Security, principal is admin object
             UsernamePasswordAuthenticationToken authToken =
                 new UsernamePasswordAuthenticationToken(
                     adminObj,
                     adminObj.getPassword(),
                     java.util.List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                 );
             SecurityContextHolder.getContext().setAuthentication(authToken);
             session.setAttribute("SPRING_SECURITY_CONTEXT", new SecurityContextImpl(authToken));
             return "redirect:/admin-home";
         } else {
             model.addAttribute("error", "Invalid credentials");
             return "admin-login";
         }
    }
    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminListPage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Load all admins
        List<admin> admins = adminRepository.findAll();
        model.addAttribute("admins", admins);

        // Add current admin info to model (if available)
        admin currentAdmin = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof AdminDetails) {
            currentAdmin = ((AdminDetails) principal).getAdmin();
        } else if (principal instanceof admin) {
            currentAdmin = (admin) principal;
        }
        model.addAttribute("admin", currentAdmin);

        // Unread notification count for admin - reuse notificationRepository if needed
        // (notificationRepository currently stores notifications for students/admins)
        int unreadCount = 0;
        if (currentAdmin != null) {
            // capture admin id in an effectively-final variable for use inside lambda
            Long currentAdminId = currentAdmin.getId();
            long cnt = notificationRepository.findAll().stream()
                .filter(n -> n.getAdmin() != null && n.getAdmin().getId() != null && n.getAdmin().getId().equals(currentAdminId) && !n.isRead())
                .count();
            unreadCount = (int) cnt;
        }
        model.addAttribute("unreadNotificationCount", unreadCount);

        return "adminList";
    }
    @PostMapping("/admin/list/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addAdmin(@RequestParam String name, @RequestParam String email, @RequestParam String password, Model model) {
        // Basic validation: email unique
        admin existing = adminRepository.findByEmailIgnoreCase(email);
        if (existing != null) {
            model.addAttribute("error", "An admin with that email already exists.");
            return "adminList"; // Show the page with error (admins list will be loaded by GET)
        }

        // Use JDBC with explicit id to avoid generated-key retrieval issues with some DB setups/drivers
        try {
            Long maxId = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 0) FROM admin", Long.class);
            long newId = (maxId == null ? 1L : maxId + 1L);
            int rows = jdbcTemplate.update("INSERT INTO admin (id, email, password, status, name) VALUES (?, ?, ?, ?, ?)", newId, email, password, "offline", name);
            if (rows <= 0) {
                logger.error("JDBC insert did not affect any rows when creating admin");
                model.addAttribute("error", "Could not create admin (DB error)");
                return "adminList";
            }
        } catch (DataAccessException dae) {
            logger.error("Failed to insert admin via JDBC", dae);
            model.addAttribute("error", "Could not create admin (DB error)");
            return "adminList";
        }

         return "redirect:/admin/list";
     }
    @PostMapping("/admin/list/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAdmin(@PathVariable Long id, Authentication authentication, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        Object principal = authentication.getPrincipal();
        Long currentAdminId = null;
        if (principal instanceof com.example.FinalProject.service.AdminDetails) {
            currentAdminId = ((com.example.FinalProject.service.AdminDetails) principal).getAdmin().getId();
        } else if (principal instanceof com.example.FinalProject.model.admin) {
            currentAdminId = ((com.example.FinalProject.model.admin) principal).getId();
        }

        if (currentAdminId != null && currentAdminId.equals(id)) {
            redirectAttrs.addFlashAttribute("error", "You cannot delete your own admin account.");
            return "redirect:/admin/list";
        }

        admin toDelete = adminRepository.findById(id).orElse(null);
        if (toDelete != null) {
            adminRepository.delete(toDelete);
            redirectAttrs.addFlashAttribute("message", "Admin deleted.");
        }
        return "redirect:/admin/list";
    }
}
