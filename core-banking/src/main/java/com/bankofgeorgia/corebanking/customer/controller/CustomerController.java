package com.bankofgeorgia.corebanking.customer.controller;

import com.bankofgeorgia.corebanking.customer.dto.CustomerRequestDTO;
import com.bankofgeorgia.corebanking.customer.dto.CustomerResponseDTO;
import com.bankofgeorgia.corebanking.customer.dto.CustomerStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.customer.dto.UpdateCustomerRequestDTO;
import com.bankofgeorgia.corebanking.customer.service.CustomerService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public ResponseEntity<CustomerResponseDTO> registerCustomer(@RequestBody CustomerRequestDTO request) {

        // log incoming request
        logger.info("Received registration request for email: {}", request.getEmail());

        CustomerResponseDTO response = customerService.registerCustomer(request);

        logger.info("Customer registration successful for email: {}", request.getEmail());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {

        logger.info("Received request to fetch all customers");

        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable String id) {

        logger.info("Received request to fetch customer with id: {}", id);

        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable String id,
            @RequestBody UpdateCustomerRequestDTO request) {

        // log update request
        logger.info("Received update request for customer id: {}", id);

        CustomerResponseDTO response = customerService.updateCustomer(id, request);

        logger.info("Customer updated successfully for id: {}", id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CustomerResponseDTO> updateCustomerStatus(
            @PathVariable String id,
            @RequestBody CustomerStatusUpdateRequestDTO request) {

        logger.info("Received status update request for customer id: {}", id);

        CustomerResponseDTO response = customerService.updateCustomerStatus(id, request);

        logger.info("Customer status updated successfully for id: {}", id);

        return ResponseEntity.ok(response);
    }
}