package com.bankofgeorgia.corebanking.customer.controller;

import com.bankofgeorgia.corebanking.customer.dto.CustomerRequestDTO;
import com.bankofgeorgia.corebanking.customer.dto.CustomerResponseDTO;
import com.bankofgeorgia.corebanking.customer.dto.CustomerStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.customer.dto.UpdateCustomerRequestDTO;
import com.bankofgeorgia.corebanking.customer.service.CustomerService;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRequestDTO request) {
        // Logs the incoming customer registration request.
        log.info("Received registration request for email: {}", request.getEmail());

        try {
            CustomerResponseDTO response = customerService.registerCustomer(request);

            log.info("Customer registration successful for email: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            log.error("Customer registration failed for email: {}", request.getEmail(), ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomers() {
        // Logs the request to fetch all customers.
        log.info("Received request to fetch all customers");

        try {
            List<CustomerResponseDTO> response = customerService.getAllCustomers();

            log.info("Fetched {} customers", response.size());
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            log.error("Failed to fetch all customers", ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable String id) {
        // Logs the customer lookup request.
        log.info("Received request to fetch customer with id: {}", id);

        try {
            CustomerResponseDTO response = customerService.getCustomerById(id);

            log.info("Customer fetched successfully for id: {}", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            log.error("Failed to fetch customer with id: {}", id, ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable String id,
            @RequestBody UpdateCustomerRequestDTO request) {
        // Logs the customer update request.
        log.info("Received update request for customer id: {}", id);

        try {
            CustomerResponseDTO response = customerService.updateCustomer(id, request);

            log.info("Customer updated successfully for id: {}", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            log.error("Customer update failed for id: {}", id, ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateCustomerStatus(
            @PathVariable String id,
            @RequestBody CustomerStatusUpdateRequestDTO request) {
        // Logs the customer status update request.
        log.info("Received status update request for customer id: {}", id);

        try {
            CustomerResponseDTO response = customerService.updateCustomerStatus(id, request);

            log.info("Customer status updated successfully for id: {}", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            log.error("Customer status update failed for id: {}", id, ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }
}