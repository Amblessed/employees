package com.amblessed.employees.service;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 09-Sep-25
 */



import com.amblessed.employees.entity.EmployeeRequest;
import com.amblessed.employees.entity.EmployeeResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface EmployeeService {

    Page<EmployeeResponse> findAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection);
    EmployeeResponse findByEmail(String email);
    List<EmployeeResponse> findByFirstName(String firstName);
    List<EmployeeResponse> findByLastName(String lastName);
    EmployeeResponse registerEmployee(EmployeeRequest employeeRequest);
    EmployeeResponse update(String id, EmployeeRequest employeeRequest);
    EmployeeResponse deleteByEmployeeId(String employeeId);
    List<EmployeeResponse> filterEmployees(String department, String position, BigDecimal salary);
    List<EmployeeResponse> exportEmployees(String department, String position, BigDecimal salary);
    EmployeeResponse findByEmployeeId(String employeeId);
}
