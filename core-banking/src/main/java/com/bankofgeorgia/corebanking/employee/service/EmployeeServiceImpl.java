package com.bankofgeorgia.corebanking.employee.service;

import com.bankofgeorgia.corebanking.common.exception.BadRequestException;
import com.bankofgeorgia.corebanking.common.exception.ConflictException;
import com.bankofgeorgia.corebanking.common.exception.ResourceNotFoundException;
import com.bankofgeorgia.corebanking.employee.dto.CreateEmployeeRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.EmployeeResponseDTO;
import com.bankofgeorgia.corebanking.employee.dto.EmployeeStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.UpdateEmployeeRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.UpdateEmployeeRoleRequestDTO;
import com.bankofgeorgia.corebanking.employee.entity.Employee;
import com.bankofgeorgia.corebanking.employee.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               BCryptPasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public EmployeeResponseDTO createEmployee(CreateEmployeeRequestDTO request) {

        log.info("Creating employee with email: {}", request.getEmail());

        // check duplicate email
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        // check duplicate username
        if (employeeRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        // check duplicate employee ID
        if (employeeRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new ConflictException("Employee ID already exists");
        }

        // map request to entity
        Employee employee = new Employee();
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setUsername(request.getUsername());
        employee.setPhone(request.getPhone());
        employee.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setEmployeeId(request.getEmployeeId());
        employee.setRole(request.getRole());
        employee.setDepartment(request.getDepartment());
        employee.setStatus("ACTIVE");
        employee.setCreatedAt(Instant.now());

        // save to database
        Employee saved = employeeRepository.save(employee);

        log.info("Employee created successfully with id: {}", saved.getId());

        return toResponse(saved);
    }

    @Override
    public List<EmployeeResponseDTO> getAllEmployees() {

        log.info("Fetching all employees");

        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public EmployeeResponseDTO getEmployeeById(String id) {

        Employee employee = employeeRepository.findByEmployeeId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        log.info("Fetched employee with id: {}", id);

        return toResponse(employee);
    }

    @Override
    public EmployeeResponseDTO updateEmployee(String id, UpdateEmployeeRequestDTO request) {

        Employee employee = employeeRepository.findByEmployeeId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        log.info("Updating employee with id: {}", id);

        // update allowed fields only
        if (request.getFirstName() != null) {
            employee.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            employee.setLastName(request.getLastName());
        }

        if (request.getPhone() != null) {
            employee.setPhone(request.getPhone());
        }

        if (request.getDateOfBirth() != null) {
            employee.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getDepartment() != null) {
            employee.setDepartment(request.getDepartment());
        }

        Employee updated = employeeRepository.save(employee);

        log.info("Employee updated successfully for id: {}", id);

        return toResponse(updated);
    }

    @Override
    public EmployeeResponseDTO updateEmployeeStatus(String id, EmployeeStatusUpdateRequestDTO request) {

        Employee employee = employeeRepository.findByEmployeeId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        String status = request.getStatus();

        // validate allowed status
        if (!"ACTIVE".equalsIgnoreCase(status) && !"INACTIVE".equalsIgnoreCase(status)) {
            throw new BadRequestException("Invalid employee status. Must be ACTIVE or INACTIVE");
        }

        log.info("Updating employee status for id: {} to {}", id, status);

        employee.setStatus(status.toUpperCase());

        Employee updated = employeeRepository.save(employee);

        return toResponse(updated);
    }

    @Override
    public EmployeeResponseDTO updateEmployeeRole(String id, UpdateEmployeeRoleRequestDTO request) {

        Employee employee = employeeRepository.findByEmployeeId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (request.getRole() == null || request.getRole().isBlank()) {
            throw new BadRequestException("Role cannot be empty");
        }

        log.info("Updating employee role for id: {} to {}", id, request.getRole());

        employee.setRole(request.getRole());

        Employee updated = employeeRepository.save(employee);

        return toResponse(updated);
    }

    // maps an Employee entity to an EmployeeResponseDTO
    private EmployeeResponseDTO toResponse(Employee employee) {
        return new EmployeeResponseDTO(
                employee.getEmployeeId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getUsername(),
                employee.getPhone(),
                employee.getDateOfBirth(),
                employee.getRole(),
                employee.getDepartment(),
                employee.getStatus(),
                employee.getCreatedAt().toString());
    }
}