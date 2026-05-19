package com.bankofgeorgia.corebanking.auth.service;

import com.bankofgeorgia.corebanking.auth.dto.EmployeeLoginRequestDTO;
import com.bankofgeorgia.corebanking.auth.dto.LoginResponseDTO;
import com.bankofgeorgia.corebanking.common.exception.UnauthorizedException;
import com.bankofgeorgia.corebanking.config.JwtUtil;
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
    private final JwtUtil jwtUtil;

    public EmployeeAuthServiceImpl(EmployeeRepository employeeRepository,
                                   BCryptPasswordEncoder passwordEncoder,
                                   JwtUtil jwtUtil) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponseDTO loginByUsername(EmployeeLoginRequestDTO request) {
        logger.info("Processing employee login for username: " + request.getUsername());

        // Look up the employee by username.
        Employee employee = employeeRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid login credentials"));

        // Compare the entered password against the stored BCrypt hash.
        if (!passwordEncoder.matches(request.getPassword(), employee.getPasswordHash())) {
            throw new UnauthorizedException("Invalid login credentials");
        }

        // Issue a JWT scoped to this employee.
        String token = jwtUtil.generateEmployeeToken(employee.getId());

        return new LoginResponseDTO("Login successful", request.getUsername(), "true", token);
    }
}
