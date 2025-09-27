package com.example.FinalProject.config;

import com.example.FinalProject.service.StudentDetailsService;
import com.example.FinalProject.service.AdminDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private StudentDetailsService studentDetailsService;

    @Autowired
    private AdminDetailsService adminDetailsService;

    @Bean
    public DaoAuthenticationProvider adminAuthenticationProvider(AdminDetailsService adminDetailsService, PasswordEncoder adminPasswordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(adminDetailsService);
        provider.setPasswordEncoder(adminPasswordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public endpoints and static resources
                .requestMatchers(
                    "/login",
                    "/custom-login",
                    "/home",
                    "/book",
                    "/admin-home",
                    "/admin-book",
                    "/user-signup",
                    "/user-signup.html",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/static/**",
                    "/",
                    "/about",
                    "/contact"
                ).permitAll()
                // Only allow POST to /user-signup without authentication
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/user-signup").permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/admin/book", true)
                .usernameParameter("email")
                .permitAll()
            )
            .logout(logout -> logout.permitAll())
            .authenticationProvider(adminAuthenticationProvider(adminDetailsService, adminPasswordEncoder()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordEncoder adminPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
