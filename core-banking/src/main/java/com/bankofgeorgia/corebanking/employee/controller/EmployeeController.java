package com.bankofgeorgia.corebanking.employee.controller;

import com.bankofgeorgia.corebanking.employee.dto.CreateEmployeeRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.EmployeeResponseDTO;
import com.bankofgeorgia.corebanking.employee.dto.EmployeeStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.UpdateEmployeeRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.UpdateEmployeeRoleRequestDTO;
import com.bankofgeorgia.corebanking.employee.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@RequestBody CreateEmployeeRequestDTO request) {
        log.info("Received request to create employee with email: {}", request.getEmail());

        EmployeeResponseDTO response = employeeService.createEmployee(request);

        log.info("Employee created successfully with employeeId: {}", response.getEmployeeId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        log.info("Received request to fetch all employees");

        List<EmployeeResponseDTO> response = employeeService.getAllEmployees();

        log.info("Fetched {} employees", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable String id) {
        log.info("Received request to fetch employee with id: {}", id);

        EmployeeResponseDTO response = employeeService.getEmployeeById(id);

        log.info("Employee fetched successfully for id: {}", id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @PathVariable String id,
            @RequestBody UpdateEmployeeRequestDTO request) {

        log.info("Received update request for employee id: {}", id);

        EmployeeResponseDTO response = employeeService.updateEmployee(id, request);

        log.info("Employee updated successfully for id: {}", id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<EmployeeResponseDTO> updateEmployeeRole(
            @PathVariable String id,
            @RequestBody UpdateEmployeeRoleRequestDTO request) {

        log.info("Received role update request for employee id: {}", id);

        EmployeeResponseDTO response = employeeService.updateEmployeeRole(id, request);

        log.info("Employee role updated successfully for id: {}", id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<EmployeeResponseDTO> updateEmployeeStatus(
            @PathVariable String id,
            @RequestBody EmployeeStatusUpdateRequestDTO request) {

        log.info("Received status update request for employee id: {}", id);

        EmployeeResponseDTO response = employeeService.updateEmployeeStatus(id, request);

        log.info("Employee status updated successfully for id: {}", id);
        return ResponseEntity.ok(response);
    }
}