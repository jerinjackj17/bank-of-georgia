package com.bankofgeorgia.corebanking.auth.dto;

// Holds the credentials sent by an employee on the login screen.
public class EmployeeLoginRequestDTO {

    private String username;
    private String password;

    public EmployeeLoginRequestDTO() {
    }

    public EmployeeLoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
