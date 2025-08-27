package com.example.FinalProject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class Login_Signup {
    @GetMapping("/sign-up")
    public String signup() { return "sign-up"; }

    @GetMapping("/login")
    public String login() { return "login"; }
}
