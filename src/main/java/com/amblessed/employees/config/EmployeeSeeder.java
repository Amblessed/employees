package com.amblessed.employees.config;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 11-Sep-25
 */


import com.amblessed.employees.entity.Employee;
import com.amblessed.employees.repository.EmployeeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;


@Profile({"dev", "test"})
@Component
@RequiredArgsConstructor
public class EmployeeSeeder implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;

    private static final int EMPLOYEE_COUNT = 10000;

    @Value("${app.seed-employees:false}")
    private boolean seedEmployees;

    @Override
    @Transactional
    public void run(String... args)  {

        if (!seedEmployees) return; // skip seeding if property is false

        // Check if table already has data
        if (employeeRepository.count() > 0) {
            // --------------------------
            //  Clear existing data
            // --------------------------
            System.out.println("Clearing existing employees...");
            employeeRepository.deleteAll();
            System.out.println("✅ Successfully cleared existing employees!");
            System.out.println(employeeRepository.count());
        }

        System.out.println("Seeding " + EMPLOYEE_COUNT + " employees...");
        List<Employee> employees = new ArrayList<>();
        Set<String> generatedEmails = new HashSet<>();

        for (int i = 0; i < EMPLOYEE_COUNT; i++) {
            Employee employee = EmployeeGenerator.createRandomEmployee();
            String email = employee.getEmail();

            // Ensure uniqueness
            while (generatedEmails.contains(email)) {
                System.out.println("Duplicate email found: " + email);
                email = email.replaceFirst("@", String.format("0%d@", EmployeeDateGenerator.randomInt(1, 9)));
                System.out.println("New email: " + email);
            }

            employee.setEmail(email);
            employees.add(employee);
            generatedEmails.add(email);
        }
        System.out.println("Generated emails: " + generatedEmails.size());
        // Save in batch
        employeeRepository.saveAll(employees);
        System.out.println("✅ Successfully seeded " + EMPLOYEE_COUNT + " employees!");
    }


}

