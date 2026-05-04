package com.bankofgeorgia.corebanking.employee.controller;

import com.bankofgeorgia.corebanking.employee.dto.CreateEmployeeRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.EmployeeResponseDTO;
import com.bankofgeorgia.corebanking.employee.dto.EmployeeStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.UpdateEmployeeRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.UpdateEmployeeRoleRequestDTO;
import com.bankofgeorgia.corebanking.employee.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody CreateEmployeeRequestDTO request) {
        logger.info("Received request to create employee with email: {}", request.getEmail());

        try {
            EmployeeResponseDTO response = employeeService.createEmployee(request);

            logger.info("Employee created successfully with employeeId: {}", response.getEmployeeId());
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Failed to create employee with email: {}", request.getEmail(), ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllEmployees() {
        logger.info("Received request to fetch all employees");

        try {
            List<EmployeeResponseDTO> response = employeeService.getAllEmployees();

            logger.info("Fetched {} employees", response.size());
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Failed to fetch all employees", ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable String id) {
        logger.info("Received request to fetch employee with id: {}", id);

        try {
            EmployeeResponseDTO response = employeeService.getEmployeeById(id);

            logger.info("Employee fetched successfully for id: {}", id);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Failed to fetch employee with id: {}", id, ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable String id,
            @RequestBody UpdateEmployeeRequestDTO request) {
        logger.info("Received update request for employee id: {}", id);

        try {
            EmployeeResponseDTO response = employeeService.updateEmployee(id, request);

            logger.info("Employee updated successfully for id: {}", id);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Failed to update employee with id: {}", id, ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateEmployeeRole(
            @PathVariable String id,
            @RequestBody UpdateEmployeeRoleRequestDTO request) {
        logger.info("Received role update request for employee id: {}", id);

        try {
            EmployeeResponseDTO response = employeeService.updateEmployeeRole(id, request);

            logger.info("Employee role updated successfully for id: {}", id);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Failed to update role for employee with id: {}", id, ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateEmployeeStatus(
            @PathVariable String id,
            @RequestBody EmployeeStatusUpdateRequestDTO request) {
        logger.info("Received status update request for employee id: {}", id);

        try {
            EmployeeResponseDTO response = employeeService.updateEmployeeStatus(id, request);

            logger.info("Employee status updated successfully for id: {}", id);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            logger.error("Failed to update status for employee with id: {}", id, ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }
}
