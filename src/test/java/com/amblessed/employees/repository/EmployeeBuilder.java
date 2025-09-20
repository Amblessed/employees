package com.amblessed.employees.repository;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 20-Sep-25
 */


import com.amblessed.employees.entity.Employee;
import com.amblessed.employees.entity.Role;
import com.amblessed.employees.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeBuilder {

    private final Employee employee;
    private static int phoneCounter = 1000; // static counter for uniqueness

    public EmployeeBuilder() {
        employee = new Employee();
        // Set default values
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@example.com");
        employee.setPhoneNumber(generateUniquePhoneNumber());
        employee.setDepartment("Engineering");
        employee.setPosition("Developer");
        employee.setSalary(BigDecimal.valueOf(60000));
        employee.setHireDate(LocalDate.now().minusYears(1));
        employee.setPerformanceReview("Good performance");
        employee.setSkills("Java, Spring Boot");
        employee.setActive(true);

        // Setup linked User and Role
        User user = new User();
        user.setUserId("EMP-00001");
        user.setEmail(employee.getEmail());
        user.setPassword("password");
        user.setActive(true);

        Role role = new Role();
        role.setUserRole("ROLE_USER");
        role.setUser(user);
        user.setRole(role);

        employee.setUser(user);
    }

    public EmployeeBuilder withFirstName(String firstName) {
        employee.setFirstName(firstName);
        return this;
    }

    public EmployeeBuilder withLastName(String lastName) {
        employee.setLastName(lastName);
        return this;
    }

    public EmployeeBuilder withEmail(String email) {
        employee.setEmail(email);
        if (employee.getUser() != null) {
            employee.getUser().setEmail(email);
        }
        return this;
    }

    public EmployeeBuilder withPhoneNumber(String phone) {
        employee.setPhoneNumber(phone);
        return this;
    }

    public EmployeeBuilder withDepartment(String department) {
        employee.setDepartment(department);
        return this;
    }

    public EmployeeBuilder withPosition(String position) {
        employee.setPosition(position);
        return this;
    }

    public EmployeeBuilder withSalary(BigDecimal salary) {
        employee.setSalary(salary);
        return this;
    }

    public EmployeeBuilder withHireDate(LocalDate hireDate) {
        employee.setHireDate(hireDate);
        return this;
    }

    public EmployeeBuilder withPerformanceReview(String review) {
        employee.setPerformanceReview(review);
        return this;
    }

    public EmployeeBuilder withSkills(String skills) {
        employee.setSkills(skills);
        return this;
    }

    public EmployeeBuilder withActive(boolean active) {
        employee.setActive(active);
        return this;
    }

    public EmployeeBuilder withUserId(String userId) {
        if (employee.getUser() == null) {
            employee.setUser(new User());
        }
        employee.getUser().setUserId(userId);
        // Also ensure role user is linked properly
        if (employee.getUser().getRole() == null) {
            Role role = new Role();
            role.setUserRole("ROLE_USER");
            role.setUser(employee.getUser());
            employee.getUser().setRole(role);
        }
        return this;
    }

    public Employee build() {
        return employee;
    }

    private String generateUniquePhoneNumber() {
        return "555-" + (phoneCounter++);
    }

}