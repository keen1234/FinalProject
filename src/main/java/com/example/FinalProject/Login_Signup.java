package com.example.FinalProject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class Login_Signup {
    @GetMapping("/user-login")
    public String showLoginPage() {
        return "user-login";
    }

    @GetMapping("/user-signup")
    public String showSignupPage() {
        return "user-signup";
    }
}



