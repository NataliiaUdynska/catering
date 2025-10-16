package com.example.Catering.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                HttpSecurity headers1 = http
                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/h2-console/**").permitAll() // ⬅️ Разрешаем доступ к H2 Console
                                .anyRequest().permitAll()
                        )
                        .csrf(csrf -> csrf.disable()) // временно отключаем CSRF
                        .headers(headers -> headers.frameOptions().disable());// ⬅️ Важно! H2 использует iframe

                return http.build();
        }
}