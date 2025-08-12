package dev.Bsit1._A.Final_Project;

        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.PostMapping;

        @Controller
        public class UserController {

            private final UserRepository userRepository;
            private final PasswordEncoder passwordEncoder;

            public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
            }

            @GetMapping("/signupForm")
            public String signupForm(Model model) {
                model.addAttribute("user", new User());
                return "signup";
            }

            @PostMapping("/signup")
            public String signup(User user) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setRole("USER");
                userRepository.save(user);
                return "redirect:/login";
            }
        }