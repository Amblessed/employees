package com.amblessed.employees.service;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 09-Sep-25
 */



import com.amblessed.employees.entity.EmployeeRequest;

import java.util.List;

public interface EmployeeService {

    List<EmployeeRequest> findAll();
    EmployeeRequest findById(long id);
    EmployeeRequest save(EmployeeRequest employeeRequest);
    EmployeeRequest update(long id, EmployeeRequest employeeRequest);
    EmployeeRequest deleteById(long id);
}
