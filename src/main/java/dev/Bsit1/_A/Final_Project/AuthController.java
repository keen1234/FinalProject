package dev.Bsit1._A.Final_Project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserDetailsServiceImpl userService;

    @PostMapping("/signup")
    public String signup(@RequestParam String username, @RequestParam String password, @RequestParam String role) {
        // Implement the signup logic in UserDetailsServiceImpl or another service
        return "Signup successful!";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        // Implement the login logic in UserDetailsServiceImpl or another service
        return "Login successful!";
    }
}