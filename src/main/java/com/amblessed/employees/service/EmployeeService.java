package com.amblessed.employees.service;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 09-Sep-25
 */



import com.amblessed.employees.entity.EmployeeRequest;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeService {

    List<EmployeeRequest> findAll();
    EmployeeRequest findById(long id);
    EmployeeRequest findByEmail(String email);
    List<EmployeeRequest> findByFirstName(String firstName);
    List<EmployeeRequest> findByLastName(String lastName);
    List<EmployeeRequest> findByDepartment(String department);
    List<EmployeeRequest> findByPosition(String position);
    List<EmployeeRequest> findByHireDate(LocalDate hireDate);
    List<EmployeeRequest> findByActive(Boolean active);
    EmployeeRequest save(EmployeeRequest employeeRequest);
    EmployeeRequest update(long id, EmployeeRequest employeeRequest);
    EmployeeRequest deleteById(long id);
}
