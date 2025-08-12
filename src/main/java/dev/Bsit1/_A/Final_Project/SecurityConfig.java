package dev.Bsit1._A.Final_Project;

            import org.springframework.context.annotation.Bean;
            import org.springframework.context.annotation.Configuration;
            import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
            import org.springframework.security.config.annotation.web.builders.HttpSecurity;
            import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
            import org.springframework.security.crypto.password.PasswordEncoder;
            import org.springframework.security.web.SecurityFilterChain;
            import org.springframework.security.authentication.AuthenticationManager;

            @Configuration
            public class SecurityConfig {

                private final UserDetailsServiceImpl userDetailsService;

                public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
                    this.userDetailsService = userDetailsService;
                }

                @Bean
                public PasswordEncoder passwordEncoder() {
                    return new BCryptPasswordEncoder();
                }

                @Bean
                public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                    http.csrf(csrf -> csrf.disable())
                        .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/", "/auth/**", "/signup", "/css/**", "/js/**", "/images/**").permitAll()
                            .anyRequest().authenticated()
                        )
                        .formLogin(form -> form
                            .loginPage("/login")
                            .defaultSuccessUrl("/")
                            .permitAll()
                        );
                    return http.build();
                }

                @Bean
                public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                    return authConfig.getAuthenticationManager();
                }
            }