package com.amblessed.employees.config;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 12-Sep-25
 */


import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeeDates(
        LocalDate hireDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
