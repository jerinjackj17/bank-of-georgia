package com.bankofgeorgia.core_banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
        public BCryptPasswordEncoder passwordEncoder(@Value("${bcrypt.strength:10}") int strength) {
        return new BCryptPasswordEncoder(strength);
    }

    @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/customers/register").permitAll()
                    .anyRequest().authenticated()
                );
            return http.build();
        }

}