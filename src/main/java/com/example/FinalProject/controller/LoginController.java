package com.example.FinalProject.controller;

import com.example.FinalProject.model.admin;
import com.example.FinalProject.repository.AdminRepository;
import com.example.FinalProject.model.Student;
import com.example.FinalProject.repository.StudentRepository;
import com.example.FinalProject.service.AdminService;
import com.example.FinalProject.service.AdminDetails;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private AdminService adminService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginController() {
        System.out.println("[LoginController] Constructor called");
    }

    @PostConstruct
    public void init() {
        System.out.println("[LoginController] @PostConstruct called - bean initialized");
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    private boolean isAdminEmail(String email) {
        if (email == null) return false;
        email = email.trim().toLowerCase();
        System.out.println("[LoginController] Checking admin pattern for email: '" + email + "'");
        if (!email.startsWith("admin") || !email.endsWith("@gmail.com")) {
            System.out.println("[LoginController] Email does not match admin pattern.");
            return false;
        }
        String namePart = email.substring(5, email.length() - "@gmail.com".length());
        boolean isAdmin = !namePart.isEmpty();
        System.out.println("[LoginController] Extracted admin name part: '" + namePart + "', isAdmin: " + isAdmin);
        return isAdmin;
    }

    private String extractAdminName(String email) {
        if (email == null) return "";
        email = email.trim();
        if (email.startsWith("admin") && email.endsWith("@gmail.com") && email.length() > 11) {
            return email.substring(5, email.length() - "@gmail.com".length());
        }
        return "";
    }

    @PostMapping("/custom-login")
    public String handleLogin(@RequestParam("email") String email, @RequestParam String password, Model model, HttpSession session) {
        System.out.println("[LoginController] handleLogin called");
        System.out.println("[LoginController] Input email (raw): '" + email + "'");
        String checkedEmail = email == null ? "" : email.trim().toLowerCase();
        System.out.println("[LoginController] Input email (trimmed/lowercased): '" + checkedEmail + "'");
        System.out.println("[LoginController] Input password: '" + password + "'");
        // Print all admin emails in DB for debugging before pattern check
        System.out.println("[LoginController] All admins in DB before pattern check:");
        for (admin a : adminRepository.findAll()) {
            System.out.println("[LoginController] DB admin email: '" + a.getEmail().toLowerCase().trim() + "'");
        }
        if (isAdminEmail(checkedEmail)) {
            String adminName = extractAdminName(checkedEmail);
            System.out.println("[LoginController] Detected admin login attempt. Admin name: '" + adminName + "'");
            admin found = adminService.getAdminByEmail(checkedEmail);
            if (found != null) {
                // Set admin status to online
                found.setStatus("online");
                adminService.save(found);
                System.out.println("[LoginController] DB returned admin: email='" + found.getEmail() + "', password='" + found.getPassword() + "', status='" + found.getStatus() + "'");
                System.out.println("[LoginController] Comparing input password '" + password + "' with DB password '" + found.getPassword() + "'");
                if (!found.getPassword().equals(password)) {
                    System.out.println("[LoginController] Incorrect admin password.");
                    model.addAttribute("error", "Incorrect admin password. Admin found: '" + found.getEmail() + "' (name: '" + adminName + "').");
                    return "login";
                }
                System.out.println("[LoginController] Admin login successful! Name: '" + adminName + "'");
                session.setAttribute("admin", found); // Store admin info in session
                System.out.println("[DEBUG] Admin set in session: " + found);
                model.addAttribute("admin", found); // Pass admin to model
                model.addAttribute("adminName", adminName);
                // Set Spring Security authentication for admin
                AdminDetails adminDetails = new AdminDetails(found);
                org.springframework.security.core.Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    adminDetails, null, adminDetails.getAuthorities()
                );
                org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
                session.setAttribute("SPRING_SECURITY_CONTEXT", org.springframework.security.core.context.SecurityContextHolder.getContext());
                System.out.println("[DEBUG] SPRING_SECURITY_CONTEXT set in session: " + session.getAttribute("SPRING_SECURITY_CONTEXT"));
                System.out.println("[DEBUG] Authentication after login: " + org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication());
                return "redirect:/admin-home";
            } else {
                System.out.println("[LoginController] Admin not found for email: '" + checkedEmail + "' (name: '" + adminName + "').");
                model.addAttribute("error", "Admin not found for email: '" + checkedEmail + "' (name: '" + adminName + "').");
                return "login";
            }
        } else {
            System.out.println("[LoginController] Email did NOT match admin pattern, falling back to student login.");
            // Print all student emails in DB for debugging
            System.out.println("[LoginController] All students in DB before query:");
            for (Student s : studentRepository.findAll()) {
                System.out.println("[LoginController] DB student email: '" + s.getEmail().toLowerCase().trim() + "'");
            }
            Student student = studentRepository.findByEmail(checkedEmail).orElse(null);
            if (student != null) {
                System.out.println("[LoginController] DB returned student: email='" + student.getEmail() + "', password='" + student.getPassword() + "'");
                System.out.println("[LoginController] Comparing input password '" + password + "' with DB password '" + student.getPassword() + "'");
                if (passwordEncoder.matches(password, student.getPassword())) {
                    System.out.println("[LoginController] Student login successful!");
                    session.setAttribute("student", student); // Store student info in session
                    // Use StudentDetails as principal for authentication
                    com.example.FinalProject.model.StudentDetails studentDetails = new com.example.FinalProject.model.StudentDetails(student);
                    org.springframework.security.core.Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        studentDetails, null, studentDetails.getAuthorities()
                    );
                    org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
                    session.setAttribute("SPRING_SECURITY_CONTEXT", org.springframework.security.core.context.SecurityContextHolder.getContext());
                    return "redirect:/home";
                } else {
                    System.out.println("[LoginController] Incorrect student password.");
                    model.addAttribute("error", "Incorrect password for user: '" + student.getEmail() + "'.");
                    return "login";
                }
            } else {
                System.out.println("[LoginController] Student not found for email: '" + checkedEmail + "'.");
                model.addAttribute("error", "User not found for email: '" + checkedEmail + "'.");
                return "login";
            }
        }
    }
}
