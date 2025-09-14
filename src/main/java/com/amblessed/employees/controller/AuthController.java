package com.amblessed.employees.controller;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 14-Sep-25
 */

import com.amblessed.employees.entity.EmployeeRequest;
import com.amblessed.employees.entity.EmployeeResponse;
import com.amblessed.employees.entity.UserDTO;
import com.amblessed.employees.service.EmployeeService;
import com.amblessed.employees.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/admin/employees")
@RequiredArgsConstructor
public class AuthController {

    private final EmployeeService employeeService;


    /*@Operation(summary = "Create employee", description = "Only admins can create employees")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody EmployeeRequest employeeRequest) {
        EmployeeResponse employeeResponse = employeeService.registerEmployee(employeeRequest);
        Map<String, Object> body = new HashMap<>();
        body.put("employee", employeeResponse);
        body.put("detail", "Employee created successfully");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(body);
    }

    @Operation(summary = "Delete employee", description = "Only admins can delete employees")
    @DeleteMapping("/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteEmployee(@PathVariable String employeeId) {
        EmployeeResponse deleted = employeeService.deleteByEmployeeId(employeeId);
        Map<String, Object> body = new HashMap<>();
        body.put("deleted_employee", deleted);
        body.put("detail", "Employee deleted successfully");
        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }*/
}
