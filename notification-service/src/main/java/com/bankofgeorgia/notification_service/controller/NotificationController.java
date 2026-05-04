package com.bankofgeorgia.notification_service.controller;

import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.bankofgeorgia.notification_service.dto.OtpNotificationRequestDTO;
import com.bankofgeorgia.notification_service.dto.NotificationResponseDTO;

import com.bankofgeorgia.notification_service.service.SmsSenderService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    Logger logger = Logger.getLogger(NotificationController.class.getName());

    private final SmsSenderService smsSenderService;

    public NotificationController(SmsSenderService smsSenderService) {
        this.smsSenderService = smsSenderService;
    }

    // Receives an OTP send request from core-banking and delegates to the SMS service.
    @PostMapping("/otp")
    public ResponseEntity<?> sendOtpNotification(@RequestBody OtpNotificationRequestDTO request) {
        logger.info("Received OTP notification request for phone number: " + request.getPhoneNumber());
        try {
            smsSenderService.sendOtp(request.getPhoneNumber(), request.getOtpCode());
            return ResponseEntity.ok(new NotificationResponseDTO("success", "OTP sent successfully"));
        } catch (RuntimeException ex) {
            logger.severe("Failed to send OTP to: " + request.getPhoneNumber() + " - " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
