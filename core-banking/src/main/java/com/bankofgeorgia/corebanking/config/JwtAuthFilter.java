package com.bankofgeorgia.corebanking.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Runs once per request, validates the JWT from the Authorization header,
// and populates the Spring Security context if the token is valid.
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Only attempt JWT validation when a Bearer token is present.
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtUtil.isValid(token)) {
                String subject = jwtUtil.extractSubject(token);
                String role = jwtUtil.extractRole(token);

                // Build a Spring Security authentication object and attach the user's role.
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        subject,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

                // Store the authentication in the security context for this request.
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // Always pass the request along the filter chain, authenticated or not.
        filterChain.doFilter(request, response);
    }
}
