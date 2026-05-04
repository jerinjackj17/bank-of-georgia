package com.bankofgeorgia.notification_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import com.twilio.Twilio;

// Loads Twilio credentials from application.properties and initializes the Twilio SDK on startup.
@Configuration
public class TwilioConfig {

    @Value("${twilio.account.sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth.token}")
    private String twilioAuthToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    // Runs automatically after Spring creates this bean — initializes the Twilio client.
    @PostConstruct
    public void initTwilio() {
        Twilio.init(twilioAccountSid, twilioAuthToken);
    }

    public String getTwilioPhoneNumber() {
        return twilioPhoneNumber;
    }
}
