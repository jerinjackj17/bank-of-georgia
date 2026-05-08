package com.bankofgeorgia.corebanking.employee.controller;

import com.bankofgeorgia.corebanking.employee.dto.CreateEmployeeRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.EmployeeResponseDTO;
import com.bankofgeorgia.corebanking.employee.dto.EmployeeStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.UpdateEmployeeRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.UpdateEmployeeRoleRequestDTO;
import com.bankofgeorgia.corebanking.employee.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody CreateEmployeeRequestDTO request) {
        log.info("Received request to create employee with email: {}", request.getEmail());

        try {
            EmployeeResponseDTO response = employeeService.createEmployee(request);

            log.info("Employee created successfully with employeeId: {}", response.getEmployeeId());
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Failed to create employee with email: {}", request.getEmail(), ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllEmployees() {
        log.info("Received request to fetch all employees");

        try {
            List<EmployeeResponseDTO> response = employeeService.getAllEmployees();

            log.info("Fetched {} employees", response.size());
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Failed to fetch all employees", ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable String id) {
        log.info("Received request to fetch employee with id: {}", id);

        try {
            EmployeeResponseDTO response = employeeService.getEmployeeById(id);

            log.info("Employee fetched successfully for id: {}", id);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Failed to fetch employee with id: {}", id, ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable String id,
            @RequestBody UpdateEmployeeRequestDTO request) {
        log.info("Received update request for employee id: {}", id);

        try {
            EmployeeResponseDTO response = employeeService.updateEmployee(id, request);

            log.info("Employee updated successfully for id: {}", id);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Failed to update employee with id: {}", id, ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateEmployeeRole(
            @PathVariable String id,
            @RequestBody UpdateEmployeeRoleRequestDTO request) {
        log.info("Received role update request for employee id: {}", id);

        try {
            EmployeeResponseDTO response = employeeService.updateEmployeeRole(id, request);

            log.info("Employee role updated successfully for id: {}", id);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Failed to update role for employee with id: {}", id, ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateEmployeeStatus(
            @PathVariable String id,
            @RequestBody EmployeeStatusUpdateRequestDTO request) {
        log.info("Received status update request for employee id: {}", id);

        try {
            EmployeeResponseDTO response = employeeService.updateEmployeeStatus(id, request);

            log.info("Employee status updated successfully for id: {}", id);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Failed to update status for employee with id: {}", id, ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }
}
