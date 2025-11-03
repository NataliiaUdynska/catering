// src/main/java/com/example/Catering/config/SecurityConfig.java
package com.example.Catering.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
                http
                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/register", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                                .requestMatchers("/profile/**").hasRole("CLIENT")
                                .anyRequest().permitAll()
                        )
                        .formLogin(form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/profile", true)
                                .permitAll()
                        )
                        .logout(logout -> logout
                                .logoutSuccessUrl("/")
                                .permitAll()
                        )
                        .csrf(csrf -> csrf.disable())
                        .headers(headers -> headers.frameOptions().disable());

                return http.build();
        }
}