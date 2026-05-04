package com.bankofgeorgia.corebanking.employee.entity;

import com.bankofgeorgia.corebanking.customer.entity.Person;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "employees")
public class Employee extends Person {

    @Id
    private String id;

    private String employeeId;
    private String role;
    private String department;
    private String status;
    private Instant createdAt;

    public Employee() {
    }

    public Employee(String firstName, String lastName, String email, String username,
                    String phone, String passwordHash, String dateOfBirth,
                    String employeeId, String role, String department,
                    String status, Instant createdAt) {
        super(firstName, lastName, email, username, phone, passwordHash, dateOfBirth);
        this.employeeId = employeeId;
        this.role = role;
        this.department = department;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
