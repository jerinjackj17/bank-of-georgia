package com.bankofgeorgia.corebanking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CoreBankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreBankingApplication.class, args);
	}
	
	

@Bean
CommandLineRunner printMongoConfig(
        @Value("${spring.data.mongodb.host:missing}") String host,
        @Value("${spring.data.mongodb.port:missing}") String port,
        @Value("${spring.data.mongodb.database:missing}") String database,
        @Value("${spring.data.mongodb.uri:missing}") String uri) {
    return args -> {
        System.out.println("MONGO_HOST=" + host);
        System.out.println("MONGO_PORT=" + port);
        System.out.println("MONGO_DB=" + database);
        System.out.println("MONGO_URI=" + uri);
    };
}
}
