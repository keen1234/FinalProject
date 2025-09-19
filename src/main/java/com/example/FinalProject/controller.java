package com.example.FinalProject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class controller {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/book")
    public String book() {
        return "book";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/user-login")
    public String showLoginPage() {
        return "user-login";
    }

    @GetMapping("/user-signup")
    public String showSignupPage() {
        return "user-signup";
    }

}
