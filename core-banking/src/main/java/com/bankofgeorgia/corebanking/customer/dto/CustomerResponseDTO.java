package com.bankofgeorgia.corebanking.customer.dto;

public class CustomerResponseDTO {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String phone;
    private String dateOfBirth;
    private String status;
    private String createdAt;

    public CustomerResponseDTO() {
    }

    public CustomerResponseDTO(String id,
                               String firstName,
                               String lastName,
                               String email,
                               String username,
                               String phone,
                               String dateOfBirth,
                               String status,
                               String createdAt) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}