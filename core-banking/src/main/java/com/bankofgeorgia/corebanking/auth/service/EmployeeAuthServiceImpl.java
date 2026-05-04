package com.bankofgeorgia.corebanking.auth.service;

import com.bankofgeorgia.corebanking.auth.dto.EmployeeLoginRequestDTO;
import com.bankofgeorgia.corebanking.auth.dto.LoginResponseDTO;
import com.bankofgeorgia.corebanking.employee.entity.Employee;
import com.bankofgeorgia.corebanking.employee.repository.EmployeeRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class EmployeeAuthServiceImpl implements EmployeeAuthService {

    Logger logger = Logger.getLogger(EmployeeAuthServiceImpl.class.getName());

    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public EmployeeAuthServiceImpl(EmployeeRepository employeeRepository,
                                   BCryptPasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponseDTO loginByUsername(EmployeeLoginRequestDTO request) {
        logger.info("Processing employee login for username: " + request.getUsername());

        // Look up the employee by username.
        Employee employee = employeeRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Employee not found with username: " + request.getUsername()));

        // Compare the entered password against the stored BCrypt hash.
        if (!passwordEncoder.matches(request.getPassword(), employee.getPasswordHash())) {
            throw new RuntimeException("Invalid password for employee: " + request.getUsername());
        }

        // Return a plain success response — no JWT token is issued yet.
        return new LoginResponseDTO("Login successful", request.getUsername(), "true");
    }
}
