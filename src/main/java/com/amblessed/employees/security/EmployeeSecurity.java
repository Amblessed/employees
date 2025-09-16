package com.amblessed.employees.security;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 13-Sep-25
 */

import com.amblessed.employees.entity.EmployeeResponse;
import com.amblessed.employees.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("employeeSecurity")
public class EmployeeSecurity {
    private final Logger log = LoggerFactory.getLogger(EmployeeSecurity.class);

    private final EmployeeService employeeService;

    public EmployeeSecurity(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Check if the authenticated user is accessing their own record
    public boolean isSelfByEmployeeId(String employeeId, Authentication authentication) {
        String userID = authentication.getName(); // username/email
        log.info("UserID: {},  Employee ID: {}, Authorities: {}", userID, employeeId, authentication.getAuthorities());
        EmployeeResponse employee = employeeService.findByEmployeeId(userID);
        return employee != null && Objects.equals(employee.getEmployeeId(), employeeId);
    }
}
