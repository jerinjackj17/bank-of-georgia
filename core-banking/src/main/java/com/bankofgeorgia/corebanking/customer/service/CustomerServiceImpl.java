package com.bankofgeorgia.corebanking.customer.service;

import com.bankofgeorgia.corebanking.customer.dto.CustomerRequestDTO;
import com.bankofgeorgia.corebanking.customer.dto.CustomerResponseDTO;
import com.bankofgeorgia.corebanking.customer.dto.CustomerStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.customer.dto.UpdateCustomerRequestDTO;
import com.bankofgeorgia.corebanking.customer.entity.Customer;
import com.bankofgeorgia.corebanking.customer.repository.CustomerRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CustomerServiceImpl(CustomerRepository customerRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CustomerResponseDTO registerCustomer(CustomerRequestDTO request) {

        logger.info("Registering customer with email: {}", request.getEmail());

        // check duplicate email
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // check duplicate username
        if (customerRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // map request to entity
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setUsername(request.getUsername());
        customer.setPhone(request.getPhone());

        // hash password before saving
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setStatus("ACTIVE");

        // set audit field
        customer.setCreatedAt(Instant.now());

        // save to database
        Customer saved = customerRepository.save(customer);

        // map entity to response DTO
        CustomerResponseDTO response = new CustomerResponseDTO(
                saved.getId(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getEmail(),
                saved.getUsername(),
                saved.getPhone(),
                saved.getDateOfBirth(),
                saved.getStatus(),
                saved.getCreatedAt().toString());

        logger.info("Customer registered successfully with id: {}", saved.getId());

        return response;
    }

    @Override
    public CustomerResponseDTO updateCustomer(String id, UpdateCustomerRequestDTO request) {

        // fetch existing customer
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        logger.info("Updating customer with id: {}", id);

        // update allowed fields only
        if (request.getFirstName() != null) {
            customer.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            customer.setLastName(request.getLastName());
        }

        if (request.getPhone() != null) {
            customer.setPhone(request.getPhone());
        }

        if (request.getDateOfBirth() != null) {
            customer.setDateOfBirth(request.getDateOfBirth());
        }

        // save updated customer
        Customer updated = customerRepository.save(customer);

        // map to response
        CustomerResponseDTO response = new CustomerResponseDTO(
                updated.getId(),
                updated.getFirstName(),
                updated.getLastName(),
                updated.getEmail(),
                updated.getUsername(),
                updated.getPhone(),
                updated.getDateOfBirth(),
                updated.getStatus(),
                updated.getCreatedAt().toString());

        logger.info("Customer updated successfully for id: {}", id);

        return response;
    }

    @Override
    public CustomerResponseDTO getCustomerById(String id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        logger.info("Fetched customer with id: {}", id);

        return new CustomerResponseDTO(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getUsername(),
                customer.getPhone(),
                customer.getDateOfBirth(),
                customer.getStatus(),
                customer.getCreatedAt().toString());
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {

        logger.info("Fetching all customers");

        return customerRepository.findAll()
                .stream()
                .map(customer -> new CustomerResponseDTO(
                        customer.getId(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail(),
                        customer.getUsername(),
                        customer.getPhone(),
                        customer.getDateOfBirth(),
                        customer.getStatus(),
                        customer.getCreatedAt().toString()))
                .toList();
    }

    @Override
    public CustomerResponseDTO updateCustomerStatus(String id, CustomerStatusUpdateRequestDTO request) {

        // fetch customer
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        String status = request.getStatus();

        // validate allowed status
        if (!"ACTIVE".equalsIgnoreCase(status) && !"BLOCKED".equalsIgnoreCase(status)) {
            throw new RuntimeException("Invalid customer status");
        }

        logger.info("Updating customer status for id: {} to {}", id, status);

        customer.setStatus(status.toUpperCase());

        Customer updated = customerRepository.save(customer);

        // map entity to response
        return new CustomerResponseDTO(
                updated.getId(),
                updated.getFirstName(),
                updated.getLastName(),
                updated.getEmail(),
                updated.getUsername(),
                updated.getPhone(),
                updated.getDateOfBirth(),
                updated.getStatus(),
                updated.getCreatedAt().toString());
    }
}