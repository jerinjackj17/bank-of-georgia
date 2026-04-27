package com.bankofgeorgia.corebanking.customer.service;

import java.util.List;

import com.bankofgeorgia.corebanking.customer.dto.CustomerRequestDTO;
import com.bankofgeorgia.corebanking.customer.dto.CustomerResponseDTO;
import com.bankofgeorgia.corebanking.customer.dto.CustomerStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.customer.dto.UpdateCustomerRequestDTO;

public interface CustomerService {

    CustomerResponseDTO registerCustomer(CustomerRequestDTO request);

    CustomerResponseDTO updateCustomer(String id, UpdateCustomerRequestDTO request);

    CustomerResponseDTO getCustomerById(String id);

    List<CustomerResponseDTO> getAllCustomers();

    CustomerResponseDTO updateCustomerStatus(String id, CustomerStatusUpdateRequestDTO request);
}