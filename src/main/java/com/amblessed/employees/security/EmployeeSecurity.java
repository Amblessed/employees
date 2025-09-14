package com.amblessed.employees.security;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 13-Sep-25
 */


import com.amblessed.employees.entity.EmployeeResponse;
import com.amblessed.employees.service.EmployeeService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("employeeSecurity")
public class EmployeeSecurity {

    private final EmployeeService employeeService;

    public EmployeeSecurity(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Check if the authenticated user is accessing their own record
    public boolean isSelfByEmployeeId(String employeeId, Authentication authentication) {
        String userID = authentication.getName(); // username/email
        System.out.println("UserID: " + userID);
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Authorities: " + authentication.getAuthorities());
        EmployeeResponse employee = employeeService.findByEmployeeId(userID);
        return employee != null && Objects.equals(employee.getEmployeeId(), employeeId);
    }
}
