package com.bankofgeorgia.corebanking.auth.controller;

import java.util.logging.Logger;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;

import com.bankofgeorgia.corebanking.auth.dto.OtpRequestDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpResponseDTO;
import com.bankofgeorgia.corebanking.auth.dto.LoginRequestDTO;
import com.bankofgeorgia.corebanking.auth.dto.LoginResponseDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpVerificationRequestDTO;
import com.bankofgeorgia.corebanking.auth.service.AuthService;
import com.bankofgeorgia.corebanking.auth.service.OtpService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/auth")
public class AuthController {
    
    Logger logger = Logger.getLogger(AuthController.class.getName());

    private final AuthService authService;
    private final OtpService otpService;

    public AuthController(AuthService authService, OtpService otpService) {
        this.authService = authService;
        this.otpService = otpService;
    }

    @PostMapping("/customer/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        logger.info("Attempting to login: " + loginRequest.getLoginId() + " with login type: " + loginRequest.getLoginType());
        LoginResponseDTO loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/customer/login/phone")
    public ResponseEntity<OtpResponseDTO> requestOtp(@RequestBody OtpRequestDTO otpRequest) {
        logger.info("Received OTP request for phone number: " + otpRequest.getPhoneNumber());
        OtpResponseDTO otpResponse = otpService.requestOtp(otpRequest);
        return ResponseEntity.ok(otpResponse);
    }

    @PostMapping("/customer/login/verifyOtp")
    public ResponseEntity<LoginResponseDTO> verifyOtp(@RequestBody OtpVerificationRequestDTO otpVerificationRequest) {
        logger.info("Received OTP verification request for phone number: " + otpVerificationRequest.getPhoneNumber());
        LoginResponseDTO loginResponse = otpService.verifyOtp(otpVerificationRequest);
        return ResponseEntity.ok(loginResponse);
    }
    
    
}
