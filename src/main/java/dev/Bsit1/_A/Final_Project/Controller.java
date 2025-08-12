package dev.Bsit1._A.Final_Project;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller
public class Controller {
    @GetMapping("/")
    public String Home() {
        return "index";
    }
    @GetMapping("/books")
    public String Books() {
        return "books";
    }
    @GetMapping("/about")
    public String About() {
        return "about";
    }
    @GetMapping("/login")
    public String Login() {
        return "login";
    }
    @GetMapping("/signup")
    public String Signup() {
        return "signup";
    }
    @GetMapping("/reset")
    public String ResetPassword() {
        return "reset";
    }
}

