package com.example.FinalProject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private StudentDetailsService studentDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/user-signup",
                                "/user-signup.html",
                                "/css/**",
                                "/images/**",
                                "/",             // homepage public
                                "/about",        // example public pages
                                "/contact"
                        ).permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/user-signup").permitAll()
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/user-signup")
                )
                .formLogin(form -> form
                        .loginPage("/login")          // GET login.html
                        .loginProcessingUrl("/login") // POST handled by Spring Security
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout(logout -> logout.permitAll())
                .userDetailsService(studentDetailsService); // <-- Explicitly wire StudentDetailsService

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
