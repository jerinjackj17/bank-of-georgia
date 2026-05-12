package com.bankofgeorgia.corebanking.client.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.bankofgeorgia.corebanking.auth.dto.SmsOtpRequest;

// Sends HTTP requests to the notification-service to deliver OTP SMS messages.
@Service
public class NotificationClientServiceImpl implements NotificationClientService {

    private final RestTemplate restTemplate;

    // URL of the notification-service OTP endpoint, loaded from application.properties.
    @Value("${notification.service.otp.url}")
    private String otpNotificationUrl;

    public NotificationClientServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendOtp(SmsOtpRequest smsOtpRequest) {
        // POST the OTP payload to the notification-service and discard the response.
        restTemplate.postForObject(otpNotificationUrl, smsOtpRequest, Void.class);
    }
}
