package com.bankofgeorgia.corebanking.employee.controller;

import com.bankofgeorgia.corebanking.employee.dto.CreateEmployeeRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.EmployeeResponseDTO;
import com.bankofgeorgia.corebanking.employee.dto.EmployeeStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.UpdateEmployeeRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.UpdateEmployeeRoleRequestDTO;
import com.bankofgeorgia.corebanking.employee.service.EmployeeService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class EmployeeControllerTest {

    private MockMvc mockMvc;

    private EmployeeService employeeService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Creates a fresh mocked service before each test.
        employeeService = mock(EmployeeService.class);

        // Creates the controller with the mocked service.
        EmployeeController employeeController = new EmployeeController(employeeService);

        // Builds MockMvc without starting the full Spring Boot application.
        mockMvc = standaloneSetup(employeeController).build();

        // Converts Java objects into JSON request bodies.
        objectMapper = new ObjectMapper();
    }

    @Test
    void createEmployee_ShouldReturnEmployee_WhenRequestIsValid() throws Exception {
        // Builds a valid employee creation request.
        CreateEmployeeRequestDTO request = buildCreateEmployeeRequest();

        // Builds the response the mocked service should return.
        EmployeeResponseDTO response = buildEmployeeResponse(
                "EMP001",
                "John",
                "Doe",
                "john@test.com",
                "john1",
                "9999999999",
                "1995-01-01",
                "TELLER",
                "RETAIL",
                "ACTIVE"
        );

        // Tells Mockito what to return when createEmployee is called.
        when(employeeService.createEmployee(any(CreateEmployeeRequestDTO.class))).thenReturn(response);

        // Sends POST request and checks status plus returned JSON fields.
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("EMP001"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@test.com"))
                .andExpect(jsonPath("$.username").value("john1"))
                .andExpect(jsonPath("$.phone").value("9999999999"))
                .andExpect(jsonPath("$.dateOfBirth").value("1995-01-01"))
                .andExpect(jsonPath("$.role").value("TELLER"))
                .andExpect(jsonPath("$.department").value("RETAIL"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Verifies that the controller called the service once.
        verify(employeeService).createEmployee(any(CreateEmployeeRequestDTO.class));
    }

    @Test
    void createEmployee_ShouldReturnServerError_WhenServiceFails() throws Exception {
        // Builds a valid request, but the service will fail.
        CreateEmployeeRequestDTO request = buildCreateEmployeeRequest();

        // Forces the mocked service to throw an exception.
        when(employeeService.createEmployee(any(CreateEmployeeRequestDTO.class)))
                .thenThrow(new RuntimeException("Email already exists"));

        // Sends POST request and expects the controller to return HTTP 500.
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Email already exists"));

        // Verifies that the service method was called.
        verify(employeeService).createEmployee(any(CreateEmployeeRequestDTO.class));
    }

    @Test
    void getAllEmployees_ShouldReturnEmployeeList_WhenEmployeesExist() throws Exception {
        // Builds one fake employee returned by the mocked service.
        EmployeeResponseDTO employee = buildEmployeeResponse(
                "EMP001",
                "John",
                "Doe",
                "john@test.com",
                "john1",
                "9999999999",
                "1995-01-01",
                "TELLER",
                "RETAIL",
                "ACTIVE"
        );

        // Tells Mockito to return a list with one employee.
        when(employeeService.getAllEmployees()).thenReturn(List.of(employee));

        // Sends GET request and checks first employee in the JSON array.
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value("EMP001"))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].email").value("john@test.com"))
                .andExpect(jsonPath("$[0].username").value("john1"))
                .andExpect(jsonPath("$[0].phone").value("9999999999"))
                .andExpect(jsonPath("$[0].role").value("TELLER"))
                .andExpect(jsonPath("$[0].department").value("RETAIL"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        // Verifies that the controller asked the service for all employees.
        verify(employeeService).getAllEmployees();
    }

    @Test
    void getAllEmployees_ShouldReturnEmptyList_WhenNoEmployeesExist() throws Exception {
        // Tells Mockito to return an empty employee list.
        when(employeeService.getAllEmployees()).thenReturn(List.of());

        // Sends GET request and checks that the JSON array is empty.
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        // Verifies that the service method was called.
        verify(employeeService).getAllEmployees();
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee_WhenEmployeeExists() throws Exception {
        // Builds a fake employee for the requested ID.
        EmployeeResponseDTO employee = buildEmployeeResponse(
                "EMP001",
                "John",
                "Doe",
                "john@test.com",
                "john1",
                "9999999999",
                "1995-01-01",
                "TELLER",
                "RETAIL",
                "ACTIVE"
        );

        // Tells Mockito to return the employee when ID 123 is requested.
        when(employeeService.getEmployeeById("123")).thenReturn(employee);

        // Sends GET request by ID and checks returned employee fields.
        mockMvc.perform(get("/api/employees/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("EMP001"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.role").value("TELLER"))
                .andExpect(jsonPath("$.department").value("RETAIL"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Verifies that the controller called getEmployeeById with the correct ID.
        verify(employeeService).getEmployeeById("123");
    }

    @Test
    void getEmployeeById_ShouldReturnServerError_WhenServiceFails() throws Exception {
        // Forces the mocked service to fail for an invalid or missing employee.
        when(employeeService.getEmployeeById("999"))
                .thenThrow(new RuntimeException("Employee not found"));

        // Sends GET request and expects server error.
        mockMvc.perform(get("/api/employees/999"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Employee not found"));

        // Verifies that the service was called with the requested ID.
        verify(employeeService).getEmployeeById("999");
    }

    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee_WhenRequestIsValid() throws Exception {
        // Builds an update request for editable employee fields.
        UpdateEmployeeRequestDTO request = new UpdateEmployeeRequestDTO();
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setPhone("9999999999");
        request.setDateOfBirth("1995-01-01");
        request.setDepartment("OPERATIONS");

        // Builds the updated response returned by the service.
        EmployeeResponseDTO response = buildEmployeeResponse(
                "EMP001",
                "Updated",
                "User",
                "john@test.com",
                "john1",
                "9999999999",
                "1995-01-01",
                "MANAGER",
                "OPERATIONS",
                "ACTIVE"
        );

        // Tells Mockito to return the updated employee.
        when(employeeService.updateEmployee(eq("123"), any(UpdateEmployeeRequestDTO.class))).thenReturn(response);

        // Sends PUT request and checks updated fields in the response.
        mockMvc.perform(put("/api/employees/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.department").value("OPERATIONS"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Verifies that updateEmployee was called with the correct ID and request type.
        verify(employeeService).updateEmployee(eq("123"), any(UpdateEmployeeRequestDTO.class));
    }

    @Test
    void updateEmployee_ShouldReturnServerError_WhenServiceFails() throws Exception {
        // Builds an update request for an employee that will fail in service.
        UpdateEmployeeRequestDTO request = new UpdateEmployeeRequestDTO();
        request.setFirstName("Updated");
        request.setLastName("User");

        // Forces the mocked service to throw an exception during update.
        when(employeeService.updateEmployee(eq("999"), any(UpdateEmployeeRequestDTO.class)))
                .thenThrow(new RuntimeException("Employee not found"));

        // Sends PUT request and expects server error.
        mockMvc.perform(put("/api/employees/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Employee not found"));

        // Verifies that the update service method was called.
        verify(employeeService).updateEmployee(eq("999"), any(UpdateEmployeeRequestDTO.class));
    }

    @Test
    void updateEmployeeStatus_ShouldReturnUpdatedEmployee_WhenStatusIsValid() throws Exception {
        // Builds a request to deactivate the employee.
        EmployeeStatusUpdateRequestDTO request = new EmployeeStatusUpdateRequestDTO();
        request.setStatus("INACTIVE");

        // Builds the response after status change.
        EmployeeResponseDTO response = buildEmployeeResponse(
                "EMP001",
                "John",
                "Doe",
                "john@test.com",
                "john1",
                "9999999999",
                "1995-01-01",
                "TELLER",
                "RETAIL",
                "INACTIVE"
        );

        // Tells Mockito to return the updated employee.
        when(employeeService.updateEmployeeStatus(eq("123"), any(EmployeeStatusUpdateRequestDTO.class)))
                .thenReturn(response);

        // Sends PUT request and checks the changed status.
        mockMvc.perform(put("/api/employees/123/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("EMP001"))
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        // Verifies that status update service method was called.
        verify(employeeService).updateEmployeeStatus(eq("123"), any(EmployeeStatusUpdateRequestDTO.class));
    }

    @Test
    void updateEmployeeStatus_ShouldReturnServerError_WhenServiceFails() throws Exception {
        // Builds a request with a status value.
        EmployeeStatusUpdateRequestDTO request = new EmployeeStatusUpdateRequestDTO();
        request.setStatus("INACTIVE");

        // Forces the mocked service to fail during status update.
        when(employeeService.updateEmployeeStatus(eq("999"), any(EmployeeStatusUpdateRequestDTO.class)))
                .thenThrow(new RuntimeException("Employee not found"));

        // Sends PUT request and expects server error.
        mockMvc.perform(put("/api/employees/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Employee not found"));

        // Verifies that status update service method was called.
        verify(employeeService).updateEmployeeStatus(eq("999"), any(EmployeeStatusUpdateRequestDTO.class));
    }

    @Test
    void updateEmployeeRole_ShouldReturnUpdatedEmployee_WhenRoleIsValid() throws Exception {
        // Builds a request to change the employee's role.
        UpdateEmployeeRoleRequestDTO request = new UpdateEmployeeRoleRequestDTO();
        request.setRole("MANAGER");

        // Builds the response after role change.
        EmployeeResponseDTO response = buildEmployeeResponse(
                "EMP001",
                "John",
                "Doe",
                "john@test.com",
                "john1",
                "9999999999",
                "1995-01-01",
                "MANAGER",
                "RETAIL",
                "ACTIVE"
        );

        // Tells Mockito to return the updated employee.
        when(employeeService.updateEmployeeRole(eq("123"), any(UpdateEmployeeRoleRequestDTO.class)))
                .thenReturn(response);

        // Sends PUT request and checks the changed role.
        mockMvc.perform(put("/api/employees/123/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("EMP001"))
                .andExpect(jsonPath("$.role").value("MANAGER"));

        // Verifies that role update service method was called.
        verify(employeeService).updateEmployeeRole(eq("123"), any(UpdateEmployeeRoleRequestDTO.class));
    }

    @Test
    void updateEmployeeRole_ShouldReturnServerError_WhenServiceFails() throws Exception {
        // Builds a request with a role value.
        UpdateEmployeeRoleRequestDTO request = new UpdateEmployeeRoleRequestDTO();
        request.setRole("MANAGER");

        // Forces the mocked service to fail during role update.
        when(employeeService.updateEmployeeRole(eq("999"), any(UpdateEmployeeRoleRequestDTO.class)))
                .thenThrow(new RuntimeException("Employee not found"));

        // Sends PUT request and expects server error.
        mockMvc.perform(put("/api/employees/999/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Employee not found"));

        // Verifies that role update service method was called.
        verify(employeeService).updateEmployeeRole(eq("999"), any(UpdateEmployeeRoleRequestDTO.class));
    }

    private CreateEmployeeRequestDTO buildCreateEmployeeRequest() {
        // Creates reusable employee creation request test data.
        CreateEmployeeRequestDTO request = new CreateEmployeeRequestDTO();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@test.com");
        request.setUsername("john1");
        request.setPhone("9999999999");
        request.setPassword("password123");
        request.setDateOfBirth("1995-01-01");
        request.setEmployeeId("EMP001");
        request.setRole("TELLER");
        request.setDepartment("RETAIL");
        return request;
    }

    private EmployeeResponseDTO buildEmployeeResponse(
            String employeeId,
            String firstName,
            String lastName,
            String email,
            String username,
            String phone,
            String dateOfBirth,
            String role,
            String department,
            String status
    ) {
        // Creates reusable employee response test data.
        return new EmployeeResponseDTO(
                employeeId,
                firstName,
                lastName,
                email,
                username,
                phone,
                dateOfBirth,
                role,
                department,
                status,
                "2026-05-04T00:00:00.000Z"
        );
    }
}
