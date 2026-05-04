package com.bankofgeorgia.corebanking.auth.controller;

import java.util.logging.Logger;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bankofgeorgia.corebanking.auth.dto.EmployeeLoginRequestDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpRequestDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpResponseDTO;
import com.bankofgeorgia.corebanking.auth.dto.LoginRequestDTO;
import com.bankofgeorgia.corebanking.auth.dto.LoginResponseDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpVerificationRequestDTO;
import com.bankofgeorgia.corebanking.auth.service.AuthService;
import com.bankofgeorgia.corebanking.auth.service.EmployeeAuthService;
import com.bankofgeorgia.corebanking.auth.service.OtpService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/auth")
public class AuthController {
    
    Logger logger = Logger.getLogger(AuthController.class.getName());

    private final AuthService authService;
    private final OtpService otpService;
    private final EmployeeAuthService employeeAuthService;

    public AuthController(AuthService authService, OtpService otpService, EmployeeAuthService employeeAuthService) {
        this.authService = authService;
        this.otpService = otpService;
        this.employeeAuthService = employeeAuthService;
    }

    // Handles username or email password login.
    @PostMapping("/customer/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        logger.info("Attempting to login: " + loginRequest.getLoginId() + " with login type: " + loginRequest.getLoginType());

        try {
            LoginResponseDTO loginResponse = authService.login(loginRequest);

            logger.info("Login successful for: " + loginRequest.getLoginId());
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException ex) {
            logger.severe("Login failed for: " + loginRequest.getLoginId() + " - " + ex.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    // Triggers an OTP SMS to the given phone number.
    @PostMapping("/customer/login/phone")
    public ResponseEntity<?> requestOtp(@RequestBody OtpRequestDTO otpRequest) {
        logger.info("Received OTP request for phone number: " + otpRequest.getPhoneNumber());

        try {
            OtpResponseDTO otpResponse = otpService.requestOtp(otpRequest);

            logger.info("OTP request successful for phone number: " + otpRequest.getPhoneNumber());
            return ResponseEntity.ok(otpResponse);
        } catch (RuntimeException ex) {
            logger.severe("OTP request failed for phone number: " + otpRequest.getPhoneNumber() + " - " + ex.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    // Verifies the OTP code entered by the customer.
    @PostMapping("/customer/login/verifyOtp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequestDTO otpVerificationRequest) {
        logger.info("Received OTP verification request for phone number: " + otpVerificationRequest.getPhoneNumber());

        try {
            LoginResponseDTO loginResponse = otpService.verifyOtp(otpVerificationRequest);

            logger.info("OTP verification successful for phone number: " + otpVerificationRequest.getPhoneNumber());
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException ex) {
            logger.severe("OTP verification failed for phone number: " + otpVerificationRequest.getPhoneNumber() + " - " + ex.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    // Handles username-based login for employees.
    @PostMapping("/employee/login/username")
    public ResponseEntity<?> employeeLogin(@RequestBody EmployeeLoginRequestDTO loginRequest) {
        logger.info("Attempting employee login for username: " + loginRequest.getUsername());

        try {
            LoginResponseDTO loginResponse = employeeAuthService.loginByUsername(loginRequest);

            logger.info("Employee login successful for: " + loginRequest.getUsername());
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException ex) {
            logger.severe("Employee login failed for: " + loginRequest.getUsername() + " - " + ex.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }
}
