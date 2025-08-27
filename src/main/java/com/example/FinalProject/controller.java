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
    public String about() { return "about"; }
    @GetMapping("/login")
    public String login() { return "login"; }
    @GetMapping("/sign-up")
    public String signup() { return "sign-up"; }
    @GetMapping("/userprofile")
    public String userprofile() { return "userprofile"; }
}
