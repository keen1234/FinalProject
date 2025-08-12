package dev.Bsit1._A.Final_Project;

                        import org.springframework.context.annotation.Bean;
                        import org.springframework.context.annotation.Configuration;
                        import org.springframework.http.HttpMethod;
                        import org.springframework.security.config.annotation.web.builders.HttpSecurity;
                        import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
                        import org.springframework.security.web.SecurityFilterChain;

                        @Configuration
                        @EnableWebSecurity
                        public class SecurityConfig {

                            @Bean
                            public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                                http
                                        .csrf(csrf -> csrf.disable())
                                        .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/", "/home", "/signup", "/reset",
                                                        "/books", "/books/**", "/about",
                                                        "/css/**", "/js/**", "/images/**").permitAll()
                                                .anyRequest().authenticated()
                                        )
                                        .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/", true)
                                                .permitAll()
                                        )
                                        .logout(logout -> logout
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll()
                                        );

                                return http.build();
                            }
                        }