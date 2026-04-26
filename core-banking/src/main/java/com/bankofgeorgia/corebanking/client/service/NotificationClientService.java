package com.bankofgeorgia.corebanking.client.service;

import com.bankofgeorgia.corebanking.auth.dto.SmsOtpRequest;

public interface NotificationClientService {
     
    void sendOtp(SmsOtpRequest smsOtpRequest);
    
} 
