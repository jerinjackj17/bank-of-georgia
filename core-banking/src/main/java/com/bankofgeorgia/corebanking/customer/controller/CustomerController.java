package com.bankofgeorgia.corebanking.customer.controller;

import com.bankofgeorgia.corebanking.customer.dto.CustomerRequestDTO;
import com.bankofgeorgia.corebanking.customer.dto.CustomerResponseDTO;
import com.bankofgeorgia.corebanking.customer.dto.CustomerStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.customer.dto.UpdateCustomerRequestDTO;
import com.bankofgeorgia.corebanking.customer.service.CustomerService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRequestDTO request) {
        // Logs the incoming customer registration request.
        logger.info("Received registration request for email: {}", request.getEmail());

        try {
            CustomerResponseDTO response = customerService.registerCustomer(request);

            logger.info("Customer registration successful for email: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            logger.error("Customer registration failed for email: {}", request.getEmail(), ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomers() {
        // Logs the request to fetch all customers.
        logger.info("Received request to fetch all customers");

        try {
            List<CustomerResponseDTO> response = customerService.getAllCustomers();

            logger.info("Fetched {} customers", response.size());
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            logger.error("Failed to fetch all customers", ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable String id) {
        // Logs the customer lookup request.
        logger.info("Received request to fetch customer with id: {}", id);

        try {
            CustomerResponseDTO response = customerService.getCustomerById(id);

            logger.info("Customer fetched successfully for id: {}", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            logger.error("Failed to fetch customer with id: {}", id, ex);

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
        logger.info("Received update request for customer id: {}", id);

        try {
            CustomerResponseDTO response = customerService.updateCustomer(id, request);

            logger.info("Customer updated successfully for id: {}", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            logger.error("Customer update failed for id: {}", id, ex);

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
        logger.info("Received status update request for customer id: {}", id);

        try {
            CustomerResponseDTO response = customerService.updateCustomerStatus(id, request);

            logger.info("Customer status updated successfully for id: {}", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            logger.error("Customer status update failed for id: {}", id, ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }
}