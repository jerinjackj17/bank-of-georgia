package com.bankofgeorgia.corebanking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// Provides a shared RestTemplate bean for making HTTP calls to other services.
// Note: this class is named WebClientConfig but provides a RestTemplate, not a WebClient.
@Configuration
public class WebClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
