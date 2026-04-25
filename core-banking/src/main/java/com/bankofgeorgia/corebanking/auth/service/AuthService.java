package com.bankofgeorgia.corebanking.service;

import java.util.logging.Logger;

import com.bankofgeorgia.corebanking.dto.OtpRequestDTO;
import com.bankofgeorgia.corebanking.dto.OtpResponseDTO;
import com.bankofgeorgia.corebanking.dto.SmsOtpRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AuthService {
    Logger logger = Logger.getLogger(AuthService.class.getName());

    private final RestTemplate restTemplate;

    @Value("${notification.service.sms.url}")
    private String smsNotificationUrl;

    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OtpResponseDTO requestOtp(OtpRequestDTO otpRequest) {
        logger.info("Processing OTP request for phone number: " + otpRequest.getPhoneNumber());
       
        String otpCode = String.valueOf((int)(Math.random() * 900000) + 100000); 

        SmsOtpRequest smsOtpRequest = new SmsOtpRequest(otpRequest.getPhoneNumber(), otpCode);

        restTemplate.postForObject(smsNotificationUrl, smsOtpRequest, Void.class);

        return new OtpResponseDTO("OTP requested successfully", otpRequest.getPhoneNumber(), "true", "300");
    }

}
