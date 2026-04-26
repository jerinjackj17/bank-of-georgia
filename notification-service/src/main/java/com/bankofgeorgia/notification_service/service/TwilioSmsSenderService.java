package com.bankofgeorgia.notification_service.service;

import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.bankofgeorgia.notification_service.config.TwilioConfig;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class TwilioSmsSenderService implements SmsSenderService {

    Logger logger = Logger.getLogger(TwilioSmsSenderService.class.getName());

    private final TwilioConfig twilioConfig;

    public TwilioSmsSenderService(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
    }

    @Override
    public void sendOtp(String phoneNumber, String otpCode) {
        
        logger.info("Sending OTP to " + phoneNumber);

        Message message = Message.creator(
            new PhoneNumber(phoneNumber),
            new PhoneNumber(twilioConfig.getTwilioPhoneNumber()),
            "Your OTP code is " + otpCode
            ).create();

        logger.info("OTP Sent to " + phoneNumber + " | Twilio SID: " + message.getSid());
    }
    
}
