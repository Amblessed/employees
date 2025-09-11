package com.amblessed.employees.controller;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 09-Sep-25
 */

import com.amblessed.employees.entity.EmployeeRequest;
import com.amblessed.employees.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeRestController {

    private final EmployeeService employeeService;

    @Operation(summary = "Get all employees", description = "Get all employees from the database")
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> findAllEmployees() {
        Map<String, Object> body = new HashMap<>();
        body.put("employees", employeeService.findAll());
        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @Operation(summary = "Get employee by id", description = "Get employee by id from the database")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findEmployeeById(@PathVariable @Min(1) long id) {

        Map<String, Object> body = new HashMap<>();
        body.put("employee", employeeService.findById(id));
        body.put("detail", "Employee found successfully");
        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }


    @Operation(summary = "Create an employee", description = "Create employee in the database")
    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> createEmployee(@Valid @RequestBody EmployeeRequest employeeRequest) {
        Map<String, Object> body = new HashMap<>();
        body.put("employee", employeeService.save(employeeRequest));
        body.put("detail", "Employee created successfully");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(body);
    }

    @Operation(summary = "Update an employee", description = "Update an employee in the database")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEmployee(@Valid @RequestBody EmployeeRequest employeeRequest, @PathVariable @Min(1) long id) {
        Map<String, Object> body = new HashMap<>();
        employeeService.findById(id);
        body.put("old_employee", employeeService.findById(id));
        body.put("updated_employee", employeeService.update(id, employeeRequest));
        body.put("detail", "Employee updated successfully");
        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }

    @Operation(summary = "Delete an employee", description = "Delete an employee in the database")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEmployee(@PathVariable @Min(1) long id) {
        Map<String, Object> body = new HashMap<>();
        body.put("deleted_employee", employeeService.deleteById(id));
        body.put("detail", "Employee deleted successfully");
        return ResponseEntity.status(HttpStatus.OK)
                .body(body);
    }
}
