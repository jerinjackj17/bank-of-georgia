package com.bankofgeorgia.corebanking.customer.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "customers")
public class Customer extends Person {

    @Id
    private String id;

    private String status; // ACTIVE, BLOCKED

    private Instant createdAt;

    public Customer() {
        // required by Mongo
    }

    public Customer(String firstName,
                    String lastName,
                    String email,
                    String username,
                    String phone,
                    String passwordHash,
                    String dateOfBirth,
                    String status,
                    Instant createdAt) {

        super(firstName, lastName, email, username, phone, passwordHash, dateOfBirth);
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}