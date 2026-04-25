package com.bankofgeorgia.corebanking.controller;

import java.util.logging.Logger;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;

import com.bankofgeorgia.corebanking.dto.OtpRequestDTO;
import com.bankofgeorgia.corebanking.dto.OtpResponseDTO;

import com.bankofgeorgia.corebanking.service.AuthService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/auth")
public class AuthController {
    
    Logger logger = Logger.getLogger(AuthController.class.getName());

    public final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/customer/login/phone")
    public ResponseEntity<OtpResponseDTO> requestOtp(@RequestBody OtpRequestDTO otpRequest) {
        logger.info("Received OTP request for phone number: " + otpRequest.getPhoneNumber());
        OtpResponseDTO otpResponse = authService.requestOtp(otpRequest);
        return ResponseEntity.ok(otpResponse);
    }
    
}
