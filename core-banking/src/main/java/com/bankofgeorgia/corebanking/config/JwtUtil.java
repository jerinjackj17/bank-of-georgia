package com.bankofgeorgia.corebanking.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.customer-expiration-ms}")
    private long customerExpirationMs;

    @Value("${jwt.employee-expiration-ms}")
    private long employeeExpirationMs;

    // Decodes the Base64 secret from application.properties into a signing key.
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Issues a JWT for a customer, valid for 15 minutes.
    public String generateCustomerToken(String subject) {
        return buildToken(subject, "CUSTOMER", customerExpirationMs);
    }

    // Issues a JWT for an employee, valid for 2 hours.
    public String generateEmployeeToken(String subject) {
        return buildToken(subject, "EMPLOYEE", employeeExpirationMs);
    }

    // Builds and signs the JWT with the given subject, role, and expiration window.
    private String buildToken(String subject, String role, long expirationMs) {
        return Jwts.builder()
                .subject(subject)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // Parses and verifies the token signature, returning all claims. Throws if invalid or expired.
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Returns the subject (user ID) embedded in the token.
    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Returns the role claim embedded in the token.
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // Returns true only if the token signature is valid and it has not expired.
    public boolean isValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
