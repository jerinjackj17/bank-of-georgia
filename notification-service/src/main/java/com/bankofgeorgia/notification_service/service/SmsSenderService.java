package com.bankofgeorgia.notification_service.service;

public interface SmsSenderService {

    void sendOtp(String phoneNumber, String otpCode);
    
}
    
