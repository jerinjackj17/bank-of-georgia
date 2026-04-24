package com.bankofgeorgia.corebanking.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Customer extends Person {

    private String customerId;

    public Customer(String firstName, String lastName, String email, String username, String phone, String passwordHash, String dateOfBirth, String customerId) {
        super(firstName, lastName, email, username, phone, passwordHash, dateOfBirth);
        this.customerId = customerId;
    }
    
}
