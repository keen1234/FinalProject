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

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.FinalProject.service.AdminDetails;
import com.example.FinalProject.service.AdminService;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof com.example.FinalProject.model.StudentDetails) {
            com.example.FinalProject.model.StudentDetails userDetails = (com.example.FinalProject.model.StudentDetails) authentication.getPrincipal();
            model.addAttribute("student", userDetails.getStudent());
        }
        return "home";
    }
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    @GetMapping("/user-signup")
    public String showSignupPage() {
        return "user-signup";
    }
    @GetMapping("/admin-book")
    public String adminBookPage(Authentication authentication, Model model) {
        Map<String, Object> admin = new HashMap<>();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof AdminDetails) {
                com.example.FinalProject.model.admin adminObj = ((AdminDetails) principal).getAdmin();
                admin.put("name", adminObj.getName());
                admin.put("email", adminObj.getEmail());
                admin.put("status", adminObj.getStatus() != null ? adminObj.getStatus() : "Active");
            } else if (principal instanceof com.example.FinalProject.model.admin) {
                com.example.FinalProject.model.admin adminObj = (com.example.FinalProject.model.admin) principal;
                admin.put("name", adminObj.getName());
                admin.put("email", adminObj.getEmail());
                admin.put("status", adminObj.getStatus() != null ? adminObj.getStatus() : "Active");
            } else if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                admin.put("name", userDetails.getUsername());
                admin.put("email", ""); // Fallback for basic UserDetails
                admin.put("status", "Active");
            } else if (principal instanceof String) {
                admin.put("name", principal);
                admin.put("email", "");
                admin.put("status", "Active");
            }
        }
        model.addAttribute("admin", admin);
        return "admin-book";
    }
    @GetMapping("/admin-home")
    public String adminHomePage(Authentication authentication, Model model, HttpSession session) {
        System.out.println("[DEBUG] /admin-home called");
        if (authentication != null) {
            System.out.println("[DEBUG] Authentication isAuthenticated: " + authentication.isAuthenticated());
            System.out.println("[DEBUG] Principal type: " + authentication.getPrincipal().getClass().getName());
            System.out.println("[DEBUG] Authorities: " + authentication.getAuthorities());
        } else {
            System.out.println("[DEBUG] Authentication is null");
        }
        System.out.println("[DEBUG] Session SPRING_SECURITY_CONTEXT: " + session.getAttribute("SPRING_SECURITY_CONTEXT"));
        System.out.println("[DEBUG] Authentication in controller: " + authentication);
        com.example.FinalProject.model.admin adminObj = null;
        if (authentication != null && authentication.isAuthenticated() && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof com.example.FinalProject.service.AdminDetails) {
                adminObj = ((com.example.FinalProject.service.AdminDetails) principal).getAdmin();
            } else if (principal instanceof com.example.FinalProject.model.admin) {
                adminObj = (com.example.FinalProject.model.admin) principal;
            }
            model.addAttribute("admin", adminObj);
            return "admin-home";
        } else {
            System.out.println("[DEBUG] Redirecting to /login");
            return "redirect:/login";
        }
    }
    @PostMapping("/admin-login")
    public String adminLogin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        com.example.FinalProject.model.admin adminObj = adminService.findByEmailAndPassword(email, password);
        if (adminObj != null) {
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
}
