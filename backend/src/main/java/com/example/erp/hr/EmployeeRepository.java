package com.example.erp.hr;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  Optional<Employee> findByEmployeeCode(String employeeCode);
}

