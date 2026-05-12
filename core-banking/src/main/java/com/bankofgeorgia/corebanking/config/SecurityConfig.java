package com.bankofgeorgia.corebanking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Creates the BCrypt password encoder used for hashing and verifying passwords.
    // Strength is read from application.properties (defaults to 10 if not set).
    @Bean
    public BCryptPasswordEncoder passwordEncoder(@Value("${bcrypt.strength:10}") int strength) {
        return new BCryptPasswordEncoder(strength);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable()) // disable CSRF for testing APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll() // allow all APIs
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
