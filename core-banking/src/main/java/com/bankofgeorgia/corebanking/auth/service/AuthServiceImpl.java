package com.bankofgeorgia.corebanking.auth.service;

import java.util.logging.Logger;

import com.bankofgeorgia.corebanking.auth.dto.LoginRequestDTO;
import com.bankofgeorgia.corebanking.auth.dto.LoginResponseDTO;
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

        Customer customer = null;

        if ("username".equalsIgnoreCase(loginRequest.getLoginType())) {
            customer = customerRepository.findByUsername(loginRequest.getLoginId())
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + loginRequest.getLoginId()));
        } else if ("email".equalsIgnoreCase(loginRequest.getLoginType())) {
            customer = customerRepository.findByEmail(loginRequest.getLoginId())
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + loginRequest.getLoginId()));
        } else {
            throw new RuntimeException("Unsupported login type: " + loginRequest.getLoginType());
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), customer.getPasswordHash())) {
            throw new RuntimeException("Invalid password for user: " + loginRequest.getLoginId());
        }

        return new LoginResponseDTO("Login successful", loginRequest.getLoginId(), "true");
    }

   

}