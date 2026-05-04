package com.bankofgeorgia.notification_service.service;

import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.bankofgeorgia.notification_service.config.TwilioConfig;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

// Sends OTP SMS messages using the Twilio API.
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

        // Build and send the SMS via Twilio — from our registered number to the customer.
        Message message = Message.creator(
            new PhoneNumber(phoneNumber),
            new PhoneNumber(twilioConfig.getTwilioPhoneNumber()),
            "Your OTP code is " + otpCode
            ).create();

        // Log the Twilio message SID for delivery tracking.
        logger.info("OTP Sent to " + phoneNumber + " | Twilio SID: " + message.getSid());
    }

}
