package com.amblessed.employees.security;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 13-Sep-25
 */

import com.amblessed.employees.entity.EmployeeResponse;
import com.amblessed.employees.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("employeeSecurity")
@RequiredArgsConstructor
public class EmployeeSecurity {

    private final Logger log = LoggerFactory.getLogger(EmployeeSecurity.class);
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_MANAGER = "ROLE_MANAGER";

    private final EmployeeService employeeService;

    /**
     * Returns true if the authenticated user has the ADMIN role.
     */
    public boolean isAdmin(Authentication authentication) {
        return hasRole(authentication, ROLE_ADMIN);
    }


    /**
     * Returns true if the authenticated user has the MANAGER role.
     */
    public boolean isManager(Authentication authentication) {
        return hasRole(authentication, ROLE_MANAGER);
    }

    /**
     * Returns true if the authenticated user is the owner of employee with given id.
     * I.e. the employee's own record.
     */
    public boolean isSelf(Authentication authentication, String employeeId) {
        if (!isAuthenticated(authentication)) return false;
        String userId = authentication.getName();
        log.info("UserID: {},  Employee ID: {}, Authorities: {}", userId, employeeId, authentication.getAuthorities());
        EmployeeResponse employee = employeeService.findByEmployeeId(userId);
        return employee != null && Objects.equals(employee.getEmployeeId(), employeeId);
    }

    /**
     * Composite: either manager OR self.
     * Returns true if the authenticated user is the owner of employee with given id or a manager.
     */
    public boolean isManagerOrSelf(Authentication authentication, String employeeId) {
        return isManager(authentication) || isSelf(authentication, employeeId);
    }

    /**
     * Composite: either manager OR admin.
     * Returns true if the authenticated user is a manager or an admin.
     */
    public boolean isAdminOrManager(Authentication authentication) {
        return isManager(authentication) || isAdmin(authentication);
    }

    /**
     * Composite: either admin OR manager OR self.
     * Returns true if the authenticated user is the owner of employee with given id or an admin or a manager.
     */
    public boolean isAdminOrManagerOrSelf(Authentication authentication, String employeeId) {
        return isAdminOrManager(authentication) || isSelf(authentication, employeeId);
    }


    /**
     * Composite: either admin OR self.
     */
    public boolean isAdminOrSelf(Authentication authentication, String employeeId) {
        return isAdmin(authentication) || isSelf(authentication, employeeId);
    }

    private boolean hasRole(Authentication authentication, String role) {
        if (isNotAuthenticated(authentication)) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> role.equals(auth.getAuthority()));
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }

    private boolean isNotAuthenticated(Authentication authentication) {
        return authentication == null || !authentication.isAuthenticated();
    }

    // Check if the authenticated user is accessing their own record
    /*public boolean isSelfByEmployeeId(String employeeId, Authentication authentication) {
        String userID = authentication.getName(); // username/email
        log.info("UserID: {},  Employee ID: {}, Authorities: {}", userID, employeeId, authentication.getAuthorities());
        EmployeeResponse employee = employeeService.findByEmployeeId(userID);
        return employee != null && Objects.equals(employee.getEmployeeId(), employeeId);
    }*/
}
