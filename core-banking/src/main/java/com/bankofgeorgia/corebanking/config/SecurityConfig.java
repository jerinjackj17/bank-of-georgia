package com.bankofgeorgia.corebanking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // Creates the BCrypt password encoder used for hashing and verifying passwords.
    @Bean
    public BCryptPasswordEncoder passwordEncoder(@Value("${bcrypt.strength:10}") int strength) {
        return new BCryptPasswordEncoder(strength);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            // Do not create or use HTTP sessions — every request must carry its own JWT.
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Auth endpoints (login, OTP) and customer registration are publicly accessible.
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/customers/register").permitAll()
                // Every other endpoint requires a valid JWT.
                .anyRequest().authenticated()
            )
            // Run JWT validation before Spring's built-in username/password filter.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
