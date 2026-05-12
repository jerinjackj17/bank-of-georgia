package com.bankofgeorgia.corebanking.auth.service;

import java.util.logging.Logger;

import com.bankofgeorgia.corebanking.auth.dto.LoginRequestDTO;
import com.bankofgeorgia.corebanking.auth.dto.LoginResponseDTO;
import com.bankofgeorgia.corebanking.common.exception.BadRequestException;
import com.bankofgeorgia.corebanking.common.exception.UnauthorizedException;
import com.bankofgeorgia.corebanking.customer.entity.Customer;
import com.bankofgeorgia.corebanking.customer.repository.CustomerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    Logger logger = Logger.getLogger(AuthServiceImpl.class.getName());

    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthServiceImpl(CustomerRepository customerRepository, BCryptPasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        logger.info("Processing login for: " + loginRequest.getLoginId() + " with login type: " + loginRequest.getLoginType());

        Customer customer;

        // Look up the customer by username or email depending on login type.
        if ("username".equalsIgnoreCase(loginRequest.getLoginType())) {
            customer = customerRepository.findByUsername(loginRequest.getLoginId())
                    .orElseThrow(() -> new UnauthorizedException("Invalid login credentials"));
        } else if ("email".equalsIgnoreCase(loginRequest.getLoginType())) {
            customer = customerRepository.findByEmail(loginRequest.getLoginId())
                    .orElseThrow(() -> new UnauthorizedException("Invalid login credentials"));
        } else {
            throw new BadRequestException("Unsupported login type: " + loginRequest.getLoginType());
        }

        // Compare the entered password against the stored BCrypt hash.
        if (!passwordEncoder.matches(loginRequest.getPassword(), customer.getPasswordHash())) {
            throw new UnauthorizedException("Invalid login credentials");
        }

        // Return a plain success response, no JWT token is issued yet.
        return new LoginResponseDTO("Login successful", loginRequest.getLoginId(), "true");
    }
}