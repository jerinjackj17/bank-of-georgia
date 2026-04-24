package com.bankofgeorgia.corebanking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerResponseDTO {
    private String message;
    private String customerId;
    private String username;
    private String status;
}
