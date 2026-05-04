package com.bankofgeorgia.corebanking.employee.dto;

public class EmployeeResponseDTO {

    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String phone;
    private String dateOfBirth;
    private String role;
    private String department;
    private String status;
    private String createdAt;

    public EmployeeResponseDTO() {
    }

    public EmployeeResponseDTO(String employeeId, String firstName, String lastName,
                               String email, String username, String phone, String dateOfBirth,
                               String role, String department, String status, String createdAt) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.role = role;
        this.department = department;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getEmployeeId() {
        return employeeId;
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

    public String getRole() {
        return role;
    }

    public String getDepartment() {
        return department;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
