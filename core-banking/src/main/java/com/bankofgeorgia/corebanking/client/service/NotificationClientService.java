package com.bankofgeorgia.corebanking.client.service;

import com.bankofgeorgia.corebanking.auth.dto.SmsOtpRequest;

// Defines the contract for calling the notification-service over HTTP.
public interface NotificationClientService {

    // Sends the OTP payload to the notification-service to trigger an SMS.
    void sendOtp(SmsOtpRequest smsOtpRequest);

}
