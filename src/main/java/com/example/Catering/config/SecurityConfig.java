package com.example.Catering.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                        // Настройка доступа к путям
                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                                .requestMatchers("/h2-console/**").permitAll() // Только для разработки!
                                .requestMatchers("/", "/menu", "/cart", "/login", "/register", "/about", "/contacts").permitAll()
                                .requestMatchers("/order", "/profile/**").authenticated()
                                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "STAFF")
                                .anyRequest().authenticated()
                        )
                        .formLogin(form -> form
                                .loginPage("/login")
                                .defaultSuccessUrl("/profile", true) // ← ПЕРЕНАПРАВЛЕНИЕ В ПРОФИЛЬ ПОСЛЕ ВХОДА
                                .failureUrl("/login?error=true")
                                .permitAll()
                        )
                        // В методе filterChain(...)
                        .logout(logout -> logout
                                .logoutUrl("/logout")              // URL для обработки выхода
                                .logoutSuccessUrl("/login?logout") // Куда перенаправлять после выхода
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .permitAll()
                        )
                        // Для H2 Console (только в dev!)
                        .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                        .headers(headers -> headers.frameOptions().disable());

                return http.build();
        }
}