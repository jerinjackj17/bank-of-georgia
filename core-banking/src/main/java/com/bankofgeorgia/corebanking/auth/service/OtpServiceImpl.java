package com.bankofgeorgia.corebanking.auth.service;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;


import com.bankofgeorgia.corebanking.auth.dto.LoginResponseDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpRequestDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpResponseDTO;
import com.bankofgeorgia.corebanking.auth.dto.OtpVerificationRequestDTO;
import com.bankofgeorgia.corebanking.auth.dto.SmsOtpRequest;

import com.bankofgeorgia.corebanking.client.service.NotificationClientService;

@Service
public class OtpServiceImpl implements OtpService {

    Logger logger = Logger.getLogger(OtpServiceImpl.class.getName());

    
    private final RedisTemplate<String, Object> redisTemplate;
    private final NotificationClientService notificationClientService;

    public OtpServiceImpl(NotificationClientService notificationClientService, RedisTemplate<String, Object> redisTemplate) {
        this.notificationClientService = notificationClientService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public OtpResponseDTO requestOtp(OtpRequestDTO otpRequest) {
        logger.info("Processing OTP request for phone number: " + otpRequest.getPhoneNumber());
       
        // Generate a random 6-digit OTP code.
        String otpCode = String.valueOf((int)(Math.random() * 900000) + 100000);

        // Build the payload to send to the notification service.
        SmsOtpRequest smsOtpRequest = new SmsOtpRequest(otpRequest.getPhoneNumber(), otpCode);

        // Trigger SMS delivery via the notification service.
        notificationClientService.sendOtp(smsOtpRequest);

        // Store the OTP in Redis keyed by phone number with a 5-minute expiry.
        redisTemplate.opsForValue().set(otpRequest.getPhoneNumber(), otpCode, 5, TimeUnit.MINUTES);

        return new OtpResponseDTO("OTP requested successfully", otpRequest.getPhoneNumber(), "true", "300");
    }

    @Override
    public LoginResponseDTO verifyOtp(OtpVerificationRequestDTO otpVerificationRequest) {
        logger.info("Verifying OTP for phone number: " + otpVerificationRequest.getPhoneNumber());

        // Fetch the stored OTP from Redis using the phone number as the key.
        String storedOtp = (String) redisTemplate.opsForValue().get(otpVerificationRequest.getPhoneNumber());

        // If the submitted OTP matches, delete it from Redis and return success.
        if (storedOtp != null && storedOtp.equals(otpVerificationRequest.getOtp())) {
            redisTemplate.delete(otpVerificationRequest.getPhoneNumber());
            return new LoginResponseDTO("OTP verified successfully", otpVerificationRequest.getPhoneNumber(), "true");
        } else {
            return new LoginResponseDTO("OTP verification failed", otpVerificationRequest.getPhoneNumber(), "false");
        }
    }
}
