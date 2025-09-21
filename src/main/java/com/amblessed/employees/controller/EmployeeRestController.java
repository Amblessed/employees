package com.amblessed.employees.controller;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 09-Sep-25
 */

import com.amblessed.employees.config.AppConstants;
import com.amblessed.employees.entity.EmployeeRequest;
import com.amblessed.employees.entity.EmployeeResponse;
import com.amblessed.employees.service.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Tag(name = "Employee API", description = "Operations related to employee management")

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeRestController {

    private final EmployeeService employeeService;

    @Operation(summary = "Get all employees", description = "Only managers can fetch paginated employees")
    @ApiResponse(responseCode = "200", description = "Employees fetched successfully")
    @ApiResponse(responseCode = "403", description = "Unauthorized")
    @GetMapping
    @PreAuthorize("@employeeSecurity.isAdminOrManager(authentication)")
    public ResponseEntity<Map<String, Object>> findAllEmployees(
            @RequestParam(defaultValue = AppConstants.PAGE) Integer page,
            @RequestParam(defaultValue = AppConstants.SIZE) Integer size,
            @RequestParam(defaultValue = AppConstants.SORT_BY_FIRSTNAME) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIRECTION) String direction
    ) {
        Page<EmployeeResponse> employees = employeeService.findAll(page, size, sortBy, direction);

        Map<String, Object> response = new HashMap<>();
        response.put("employees", employees.getContent());
        response.put("currentPage", employees.getNumber());
        response.put("totalPages", employees.getTotalPages());
        response.put("totalElements", employees.getTotalElements());
        response.put("size", employees.getSize());
        response.put("numberOfElements", employees.getNumberOfElements());
        response.put("first", employees.isFirst());
        response.put("last", employees.isLast());
        response.put("empty", employees.isEmpty());

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get employee by ID", description = "Self-access or managers/admins only")
    @GetMapping("/id/{employeeId}")
    @PreAuthorize("@employeeSecurity.isAdminOrManagerOrSelf(authentication, #employeeId)")
    public ResponseEntity<Map<String, Object>> findByEmployeeId(@PathVariable String employeeId) {
        EmployeeResponse employee = employeeService.findByEmployeeId(employeeId);
        Map<String, Object> response = Map.of(
                "employee", employee,
                "detail", "Employee found successfully"
        );
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Search employees", description = "Managers/Admins can search by department, position or salary")
    @GetMapping("/search")
    @PreAuthorize("@employeeSecurity.isAdminOrManager(authentication)")
    public ResponseEntity<Map<String, Object>> searchEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false, defaultValue = AppConstants.DEFAULT_SALARY) BigDecimal salary
    ) {
        List<EmployeeResponse> results = employeeService.filterEmployees(department, position, salary);
        Map<String, Object> response = Map.of(
                "employees", results,
                "count", results.size()
        );
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Export employees as JSON", description = "Admins/managers can export filtered list")
    @GetMapping("/download")
    @PreAuthorize("@employeeSecurity.isAdminOrManager(authentication)")
    public ResponseEntity<byte[]> exportEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) BigDecimal salary
    ) throws JsonProcessingException {

        List<EmployeeResponse> employees = employeeService.exportEmployees(department, position, salary);

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        byte[] jsonBytes = mapper.writeValueAsBytes(employees);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename("employees.json").build());
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.ok()
                .headers(headers)
                .body(jsonBytes);
    }

    @Operation(summary = "Update employee", description = "Self or manager-only update")
    @PutMapping("/id/{employeeId}")
    @PreAuthorize("@employeeSecurity.isAdminOrManagerOrSelf(authentication, #employeeId)")
    public ResponseEntity<Map<String, Object>> updateEmployee(
            @Valid @RequestBody EmployeeRequest employeeRequest,
            @PathVariable String employeeId
    ) {
        EmployeeResponse oldData = employeeService.findByEmployeeId(employeeId);
        EmployeeResponse updated = employeeService.update(employeeId, employeeRequest);

        Map<String, Object> response = Map.of(
                "old_employee", oldData,
                "updated_employee", updated,
                "detail", "Employee updated successfully"
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create employee", description = "Only admins can create new employees")
    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("@employeeSecurity.isAdmin(authentication)")
    public ResponseEntity<Map<String, Object>> createEmployee(
            @Valid @RequestBody EmployeeRequest employeeRequest
    ) {
        EmployeeResponse employee = employeeService.registerEmployee(employeeRequest);
        Map<String, Object> response = Map.of(
                "employee", employee,
                "detail", "Employee created successfully"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Delete employee", description = "Only admins can delete employee records")
    @DeleteMapping("/id/{employeeId}")
    @PreAuthorize("@employeeSecurity.isAdmin(authentication)")
    public ResponseEntity<Map<String, Object>> deleteEmployee(@PathVariable String employeeId) {
        EmployeeResponse deleted = employeeService.deleteByEmployeeId(employeeId);
        Map<String, Object> response = Map.of(
                "deleted_employee", deleted,
                "detail", "Employee deleted successfully"
        );
        return ResponseEntity.ok(response);
    }
}