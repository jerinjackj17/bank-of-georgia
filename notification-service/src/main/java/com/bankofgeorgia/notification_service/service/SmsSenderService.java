package com.bankofgeorgia.notification_service.service;

// Defines the contract for sending SMS messages.
public interface SmsSenderService {

    // Sends the given OTP code as a text message to the given phone number.
    void sendOtp(String phoneNumber, String otpCode);

}
