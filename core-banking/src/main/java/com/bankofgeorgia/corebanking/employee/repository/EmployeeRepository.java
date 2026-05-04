package com.bankofgeorgia.corebanking.employee.repository;

import com.bankofgeorgia.corebanking.employee.entity.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EmployeeRepository extends MongoRepository<Employee, String> {

    Optional<Employee> findByUsername(String username);

    Optional<Employee> findByEmployeeId(String employeeId);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmployeeId(String employeeId);
}
