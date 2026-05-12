package com.bankofgeorgia.corebanking.customer.controller;

import com.bankofgeorgia.corebanking.customer.dto.CustomerRequestDTO;
import com.bankofgeorgia.corebanking.customer.dto.CustomerResponseDTO;
import com.bankofgeorgia.corebanking.customer.dto.CustomerStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.customer.dto.UpdateCustomerRequestDTO;
import com.bankofgeorgia.corebanking.customer.service.CustomerService;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<CustomerResponseDTO> registerCustomer(@RequestBody CustomerRequestDTO request) {
        log.info("Received registration request for email: {}", request.getEmail());

        CustomerResponseDTO response = customerService.registerCustomer(request);

        log.info("Customer registration successful for email: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        log.info("Received request to fetch all customers");

        List<CustomerResponseDTO> response = customerService.getAllCustomers();

        log.info("Fetched {} customers", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable String id) {
        log.info("Received request to fetch customer with id: {}", id);

        CustomerResponseDTO response = customerService.getCustomerById(id);

        log.info("Customer fetched successfully for id: {}", id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable String id,
            @RequestBody UpdateCustomerRequestDTO request) {

        log.info("Received update request for customer id: {}", id);

        CustomerResponseDTO response = customerService.updateCustomer(id, request);

        log.info("Customer updated successfully for id: {}", id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CustomerResponseDTO> updateCustomerStatus(
            @PathVariable String id,
            @RequestBody CustomerStatusUpdateRequestDTO request) {

        log.info("Received status update request for customer id: {}", id);

        CustomerResponseDTO response = customerService.updateCustomerStatus(id, request);

        log.info("Customer status updated successfully for id: {}", id);
        return ResponseEntity.ok(response);
    }
}