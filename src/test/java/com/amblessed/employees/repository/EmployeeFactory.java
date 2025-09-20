package com.amblessed.employees.repository;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 20-Sep-25
 */


import com.amblessed.employees.entity.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmployeeFactory {

    private static final String[] DEPARTMENTS = {"Engineering", "Sales", "Marketing", "HR", "Finance"};
    private static final String[] POSITIONS = {"Developer", "Manager", "Analyst", "Consultant", "Administrator"};
    private static final Random RANDOM = new Random();

    public static List<Employee> createEmployees(int count) {
        List<Employee> employees = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String department = DEPARTMENTS[RANDOM.nextInt(DEPARTMENTS.length)];
            String position = POSITIONS[RANDOM.nextInt(POSITIONS.length)];

            Employee emp = new EmployeeBuilder()
                    .withUserId(String.format("EMP-%05d", i))
                    .withEmail("employee" + i + "@example.com")
                    .withFirstName("FirstName" + i)
                    .withLastName("LastName" + i)
                    .withDepartment(department)
                    .withPosition(position)
                    .withSalary(BigDecimal.valueOf(40000 + RANDOM.nextInt(60000)))
                    .withHireDate(LocalDate.now().minusDays(RANDOM.nextInt(1000)))
                    .withPhoneNumber("555-" + (1000 + i))
                    .build();

            employees.add(emp);
        }
        return employees;
    }
}
