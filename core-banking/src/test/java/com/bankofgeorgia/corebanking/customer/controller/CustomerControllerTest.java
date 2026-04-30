package com.bankofgeorgia.corebanking.customer.controller;

import com.bankofgeorgia.corebanking.customer.dto.CustomerRequestDTO;
import com.bankofgeorgia.corebanking.customer.dto.CustomerResponseDTO;
import com.bankofgeorgia.corebanking.customer.dto.CustomerStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.customer.dto.UpdateCustomerRequestDTO;
import com.bankofgeorgia.corebanking.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class CustomerControllerTest {

    private MockMvc mockMvc;

    private CustomerService customerService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Creates a fresh mocked service before each test.
        customerService = mock(CustomerService.class);

        // Creates the controller with the mocked service.
        CustomerController customerController = new CustomerController(customerService);

        // Builds MockMvc without starting the full Spring Boot application.
        mockMvc = standaloneSetup(customerController).build();

        // Converts Java objects into JSON request bodies.
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerCustomer_ShouldReturnCustomer_WhenRequestIsValid() throws Exception {
        // Builds a valid registration request body.
        CustomerRequestDTO request = buildRegistrationRequest();

        // Builds the response the mocked service should return.
        CustomerResponseDTO response = buildCustomerResponse(
                "123",
                "John",
                "Doe",
                "john@test.com",
                "john1",
                "9999999999",
                "1995-01-01",
                "ACTIVE"
        );

        // Tells Mockito what to return when registerCustomer is called.
        when(customerService.registerCustomer(any(CustomerRequestDTO.class))).thenReturn(response);

        // Sends POST request and checks status plus returned JSON fields.
        mockMvc.perform(post("/api/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@test.com"))
                .andExpect(jsonPath("$.username").value("john1"))
                .andExpect(jsonPath("$.phone").value("9999999999"))
                .andExpect(jsonPath("$.dateOfBirth").value("1995-01-01"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Verifies that the controller called the service once.
        verify(customerService).registerCustomer(any(CustomerRequestDTO.class));
    }

    @Test
    void registerCustomer_ShouldReturnServerError_WhenServiceFails() throws Exception {
        // Builds a valid request, but the service will fail.
        CustomerRequestDTO request = buildRegistrationRequest();

        // Forces the mocked service to throw an exception.
        when(customerService.registerCustomer(any(CustomerRequestDTO.class)))
                .thenThrow(new RuntimeException("Email already exists"));

        // Sends POST request and expects the controller to return HTTP 500.
        mockMvc.perform(post("/api/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Email already exists"));

        // Verifies that registration service method was called.
        verify(customerService).registerCustomer(any(CustomerRequestDTO.class));
    }

    @Test
    void getAllCustomers_ShouldReturnCustomerList_WhenCustomersExist() throws Exception {
        // Builds one fake customer returned by the mocked service.
        CustomerResponseDTO customer = buildCustomerResponse(
                "123",
                "Jerin",
                "Jack",
                "jerin@test.com",
                "jerin",
                "3129189649",
                "2000-01-01",
                "ACTIVE"
        );

        // Tells Mockito to return a list with one customer.
        when(customerService.getAllCustomers()).thenReturn(List.of(customer));

        // Sends GET request and checks first customer in the JSON array.
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("123"))
                .andExpect(jsonPath("$[0].firstName").value("Jerin"))
                .andExpect(jsonPath("$[0].lastName").value("Jack"))
                .andExpect(jsonPath("$[0].email").value("jerin@test.com"))
                .andExpect(jsonPath("$[0].username").value("jerin"))
                .andExpect(jsonPath("$[0].phone").value("3129189649"))
                .andExpect(jsonPath("$[0].dateOfBirth").value("2000-01-01"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        // Verifies that the controller asked the service for all customers.
        verify(customerService).getAllCustomers();
    }

    @Test
    void getAllCustomers_ShouldReturnEmptyList_WhenNoCustomersExist() throws Exception {
        // Tells Mockito to return an empty customer list.
        when(customerService.getAllCustomers()).thenReturn(List.of());

        // Sends GET request and checks that the JSON array is empty.
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        // Verifies that the service method was called.
        verify(customerService).getAllCustomers();
    }

    @Test
    void getCustomerById_ShouldReturnCustomer_WhenCustomerExists() throws Exception {
        // Builds a fake customer for the requested ID.
        CustomerResponseDTO customer = buildCustomerResponse(
                "123",
                "Jerin",
                "Jack",
                "jerin@test.com",
                "jerin",
                "3129189649",
                "2000-01-01",
                "ACTIVE"
        );

        // Tells Mockito to return the customer when ID 123 is requested.
        when(customerService.getCustomerById("123")).thenReturn(customer);

        // Sends GET request by ID and checks returned customer fields.
        mockMvc.perform(get("/api/customers/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.firstName").value("Jerin"))
                .andExpect(jsonPath("$.lastName").value("Jack"))
                .andExpect(jsonPath("$.email").value("jerin@test.com"))
                .andExpect(jsonPath("$.username").value("jerin"))
                .andExpect(jsonPath("$.phone").value("3129189649"))
                .andExpect(jsonPath("$.dateOfBirth").value("2000-01-01"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Verifies that the controller called getCustomerById with the correct ID.
        verify(customerService).getCustomerById("123");
    }

    @Test
    void getCustomerById_ShouldReturnServerError_WhenServiceFails() throws Exception {
        // Forces the mocked service to fail for an invalid or missing customer.
        when(customerService.getCustomerById("999"))
                .thenThrow(new RuntimeException("Customer not found"));

        // Sends GET request and expects server error because controller has no specific not-found handler.
        mockMvc.perform(get("/api/customers/999"))
                .andExpect(status().isInternalServerError());

        // Verifies that the service was called with the requested ID.
        verify(customerService).getCustomerById("999");
    }

    @Test
    void updateCustomer_ShouldReturnUpdatedCustomer_WhenRequestIsValid() throws Exception {
        // Builds an update request for editable customer fields.
        UpdateCustomerRequestDTO request = new UpdateCustomerRequestDTO();
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setPhone("9999999999");
        request.setDateOfBirth("2000-01-01");

        // Builds the updated response returned by the service.
        CustomerResponseDTO response = buildCustomerResponse(
                "123",
                "Updated",
                "User",
                "jerin@test.com",
                "jerin",
                "9999999999",
                "2000-01-01",
                "ACTIVE"
        );

        // Tells Mockito to return the updated customer.
        when(customerService.updateCustomer(eq("123"), any(UpdateCustomerRequestDTO.class))).thenReturn(response);

        // Sends PUT request and checks updated fields in the response.
        mockMvc.perform(put("/api/customers/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.phone").value("9999999999"))
                .andExpect(jsonPath("$.dateOfBirth").value("2000-01-01"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Verifies that updateCustomer was called with the correct ID and request type.
        verify(customerService).updateCustomer(eq("123"), any(UpdateCustomerRequestDTO.class));
    }

    @Test
    void updateCustomer_ShouldReturnServerError_WhenServiceFails() throws Exception {
        // Builds an update request for a customer that will fail in service.
        UpdateCustomerRequestDTO request = new UpdateCustomerRequestDTO();
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setPhone("9999999999");
        request.setDateOfBirth("2000-01-01");

        // Forces the mocked service to throw an exception during update.
        when(customerService.updateCustomer(eq("999"), any(UpdateCustomerRequestDTO.class)))
                .thenThrow(new RuntimeException("Customer not found"));

        // Sends PUT request and expects server error because update exception is not specially handled.
        mockMvc.perform(put("/api/customers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        // Verifies that the update service method was called.
        verify(customerService).updateCustomer(eq("999"), any(UpdateCustomerRequestDTO.class));
    }

    @Test
    void updateCustomerStatus_ShouldReturnUpdatedCustomer_WhenStatusIsValid() throws Exception {
        // Builds a request to block the customer.
        CustomerStatusUpdateRequestDTO request = new CustomerStatusUpdateRequestDTO();
        request.setStatus("BLOCKED");

        // Builds the response after status change.
        CustomerResponseDTO response = buildCustomerResponse(
                "123",
                "Jerin",
                "Jack",
                "jerin@test.com",
                "jerin",
                "3129189649",
                "2000-01-01",
                "BLOCKED"
        );

        // Tells Mockito to return the blocked customer.
        when(customerService.updateCustomerStatus(eq("123"), any(CustomerStatusUpdateRequestDTO.class)))
                .thenReturn(response);

        // Sends PUT request and checks the changed status.
        mockMvc.perform(put("/api/customers/123/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.status").value("BLOCKED"));

        // Verifies that status update service method was called.
        verify(customerService).updateCustomerStatus(eq("123"), any(CustomerStatusUpdateRequestDTO.class));
    }

    @Test
    void updateCustomerStatus_ShouldReturnServerError_WhenServiceFails() throws Exception {
        // Builds a request with a status value.
        CustomerStatusUpdateRequestDTO request = new CustomerStatusUpdateRequestDTO();
        request.setStatus("BLOCKED");

        // Forces the mocked service to fail during status update.
        when(customerService.updateCustomerStatus(eq("999"), any(CustomerStatusUpdateRequestDTO.class)))
                .thenThrow(new RuntimeException("Customer not found"));

        // Sends PUT request and expects server error.
        mockMvc.perform(put("/api/customers/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        // Verifies that status update service method was called.
        verify(customerService).updateCustomerStatus(eq("999"), any(CustomerStatusUpdateRequestDTO.class));
    }

    private CustomerRequestDTO buildRegistrationRequest() {
        // Creates reusable registration request test data.
        CustomerRequestDTO request = new CustomerRequestDTO();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@test.com");
        request.setUsername("john1");
        request.setPhone("9999999999");
        request.setPassword("password123");
        request.setDateOfBirth("1995-01-01");
        return request;
    }

    private CustomerResponseDTO buildCustomerResponse(
            String id,
            String firstName,
            String lastName,
            String email,
            String username,
            String phone,
            String dateOfBirth,
            String status
    ) {
        // Creates reusable customer response test data.
        return new CustomerResponseDTO(
                id,
                firstName,
                lastName,
                email,
                username,
                phone,
                dateOfBirth,
                status,
                "2026-04-25T06:24:28.724Z"
        );
    }
}