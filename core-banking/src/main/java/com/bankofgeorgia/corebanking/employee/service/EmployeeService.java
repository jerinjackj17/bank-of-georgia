package com.bankofgeorgia.corebanking.employee.service;

import com.bankofgeorgia.corebanking.employee.dto.CreateEmployeeRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.EmployeeResponseDTO;
import com.bankofgeorgia.corebanking.employee.dto.EmployeeStatusUpdateRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.UpdateEmployeeRequestDTO;
import com.bankofgeorgia.corebanking.employee.dto.UpdateEmployeeRoleRequestDTO;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDTO createEmployee(CreateEmployeeRequestDTO request);

    List<EmployeeResponseDTO> getAllEmployees();

    EmployeeResponseDTO getEmployeeById(String id);

    EmployeeResponseDTO updateEmployee(String id, UpdateEmployeeRequestDTO request);

    EmployeeResponseDTO updateEmployeeStatus(String id, EmployeeStatusUpdateRequestDTO request);

    EmployeeResponseDTO updateEmployeeRole(String id, UpdateEmployeeRoleRequestDTO request);
}
