package com.bankofgeorgia.core_banking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String phone;
    private String password;
    private String dateOfBirth; 
}
