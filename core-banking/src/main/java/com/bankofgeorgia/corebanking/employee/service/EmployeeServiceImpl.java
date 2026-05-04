package com.bankofgeorgia.corebanking.employee.service;

import com.bankofgeorgia.corebanking.employee.dto.CreateEmployeeRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.EmployeeResponseDTO;
import com.bankofgeorgia.corebanking.employee.dto.EmployeeStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.UpdateEmployeeRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.UpdateEmployeeRoleRequestDTO;
import com.bankofgeorgia.corebanking.employee.entity.Employee;
import com.bankofgeorgia.corebanking.employee.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               BCryptPasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public EmployeeResponseDTO createEmployee(CreateEmployeeRequestDTO request) {

        logger.info("Creating employee with email: {}", request.getEmail());

        // check duplicate email
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        // check duplicate username
        if (employeeRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        // check duplicate employee ID
        if (employeeRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee ID already exists");
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

        logger.info("Employee created successfully with id: {}", saved.getId());

        return toResponse(saved);
    }

    @Override
    public List<EmployeeResponseDTO> getAllEmployees() {

        logger.info("Fetching all employees");

        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public EmployeeResponseDTO getEmployeeById(String id) {

        Employee employee = employeeRepository.findByEmployeeId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        logger.info("Fetched employee with id: {}", id);

        return toResponse(employee);
    }

    @Override
    public EmployeeResponseDTO updateEmployee(String id, UpdateEmployeeRequestDTO request) {

        Employee employee = employeeRepository.findByEmployeeId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        logger.info("Updating employee with id: {}", id);

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

        logger.info("Employee updated successfully for id: {}", id);

        return toResponse(updated);
    }

    @Override
    public EmployeeResponseDTO updateEmployeeStatus(String id, EmployeeStatusUpdateRequestDTO request) {

        Employee employee = employeeRepository.findByEmployeeId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        String status = request.getStatus();

        // validate allowed status
        if (!"ACTIVE".equalsIgnoreCase(status) && !"INACTIVE".equalsIgnoreCase(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid employee status. Must be ACTIVE or INACTIVE");
        }

        logger.info("Updating employee status for id: {} to {}", id, status);

        employee.setStatus(status.toUpperCase());

        Employee updated = employeeRepository.save(employee);

        return toResponse(updated);
    }

    @Override
    public EmployeeResponseDTO updateEmployeeRole(String id, UpdateEmployeeRoleRequestDTO request) {

        Employee employee = employeeRepository.findByEmployeeId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        if (request.getRole() == null || request.getRole().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role cannot be empty");
        }

        logger.info("Updating employee role for id: {} to {}", id, request.getRole());

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
