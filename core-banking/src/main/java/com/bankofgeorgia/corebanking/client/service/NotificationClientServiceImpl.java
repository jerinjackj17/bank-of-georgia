package com.bankofgeorgia.corebanking.client.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.bankofgeorgia.corebanking.auth.dto.SmsOtpRequest;

@Service
public class NotificationClientServiceImpl implements NotificationClientService {

    private final RestTemplate restTemplate;

    @Value("${notification.service.otp.url}")
    private String otpNotificationUrl;

    public NotificationClientServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendOtp(SmsOtpRequest smsOtpRequest) {
        restTemplate.postForObject(otpNotificationUrl, smsOtpRequest, Void.class);
    }
}
