package com.bankofgeorgia.corebanking.service;

import com.bankofgeorgia.corebanking.dto.CustomerRequestDTO;
import com.bankofgeorgia.corebanking.dto.CustomerResponseDTO;
import com.bankofgeorgia.corebanking.entity.Customer;
import com.bankofgeorgia.corebanking.dao.CustomerDao;

import java.util.logging.Logger;

import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    Logger logger = Logger.getLogger(CustomerService.class.getName());

    private final CustomerDao customerDao;

    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public CustomerResponseDTO registerCustomer(CustomerRequestDTO customerRequest) {
        logger.info("Registering customer with email: " + customerRequest.getEmail());
       
        Customer customer = new Customer();
        customer.setFirstName(customerRequest.getFirstName());
        customer.setLastName(customerRequest.getLastName());
        customer.setEmail(customerRequest.getEmail());
        customer.setUsername(customerRequest.getUsername());
        customer.setPhone(customerRequest.getPhone());
        customer.setPassword(customerRequest.getPassword()); 
        customer.setDateOfBirth(customerRequest.getDateOfBirth());

        Customer savedCustomer = customerDao.save(customer);

        CustomerResponseDTO responseDTO = new CustomerResponseDTO();
    
        responseDTO.setMessage("Customer registered successfully");
        responseDTO.setCustomerId(savedCustomer.getCustomerId());
        responseDTO.setUsername(savedCustomer.getUsername());
        responseDTO.setStatus("ACTIVE");

        return responseDTO;
    }


}
