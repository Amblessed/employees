package com.amblessed.employees.config;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 11-Sep-25
 */


import com.amblessed.employees.entity.Employee;
import com.amblessed.employees.entity.Role;
import com.amblessed.employees.entity.User;
import com.amblessed.employees.repository.EmployeeRepository;
import com.amblessed.employees.repository.RoleRepository;
import com.amblessed.employees.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;


@Profile({"dev", "test"})
@Component
@RequiredArgsConstructor
@Order(1)
public class EmployeeSeeder implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final Logger log = LoggerFactory.getLogger(EmployeeSeeder.class);
    Random random = new Random();

    private static final int EMPLOYEE_COUNT = 400;

    @Value("${app.seed-employees:false}")
    private boolean seedEmployees;

    @Value("${user.details.path:src/test/resources/user_details.json}")
    private String userDetailsPath;

    private static final int BATCH_SIZE = 50; // batch save size

    @Override
    @Transactional
    public void run(String... args)  throws Exception {

        if (!seedEmployees){
            log.info("Employee seeding skipped (app.seed-employees=false)");
            return; // skip seeding if property is false
        }

        // Clear existing data
        log.info("Clearing existing data...");
        roleRepository.deleteAllInBatch();
        employeeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        log.info("Cleared previous data!");

        System.out.println("Seeding " + EMPLOYEE_COUNT + " employees...");
        System.out.flush();
        log.info("Seeding {} employees...", EMPLOYEE_COUNT);

        List<Employee> employeesBatch = new ArrayList<>();

        Set<String> generatedEmails = new HashSet<>();
        Set<String> generatedPhoneNumbers = new HashSet<>();
        Set<String> generatedUserIds = new HashSet<>();
        Map<String, Object> emailPasswordMap = new HashMap<>(); // store plain-text passwords

        for (int i = 0; i < EMPLOYEE_COUNT; i++) {
            Employee employee = EmployeeGenerator.createRandomEmployee();
            String email = employee.getEmail();
            String phoneNumber = employee.getPhoneNumber();
            String userId = generateUniqueEmployeeId();

            // Ensure uniqueness
            while (generatedEmails.contains(email)) {
                email = email.replaceFirst("@", String.format("0%d@", EmployeeDateGenerator.randomInt(1, 9)));
            }
            generatedEmails.add(email);

            while (generatedPhoneNumbers.contains(phoneNumber)) {
                phoneNumber = EmployeeGenerator.generateUniquePhoneNumber();
            }
            generatedPhoneNumbers.add(phoneNumber);

            while (generatedUserIds.contains(userId)) {
                userId = generateUniqueEmployeeId();
            }
            generatedUserIds.add(userId);

            employee.setEmail(email);
            employee.setPhoneNumber(phoneNumber);


            // Generate User
            String plainPassword = EmployeeGenerator.generateValidPassword();
            String encodedPassword = passwordEncoder.encode(plainPassword);
            String randomRole = generateRandomRole();

            // ✅ Save mapping for pytest
            Map<String, String> employeeDetails = new HashMap<>();
            employeeDetails.put("email", employee.getEmail());
            employeeDetails.put("password", plainPassword);
            employeeDetails.put("role", randomRole);
            emailPasswordMap.put(userId, employeeDetails);

            // 5. Create corresponding User
            User user = new User();
            user.setUserId(userId); // FK link
            user.setEmail(employee.getEmail());
            user.setPassword(encodedPassword);
            user.setActive(true);

            // 6. Assign Role
            Role role = new Role();
            role.setUser(user);
            role.setEmail(user.getEmail());
            role.setUserRole(randomRole);
            user.getRoles().add(role);
            employee.setUser(user);
            user.setEmployee(employee);

            employeesBatch.add(employee);

            // Save in batches
            if (employeesBatch.size() >= BATCH_SIZE) {
                employeeRepository.saveAll(employeesBatch);
                employeesBatch.clear();
                System.out.println("Seeded " + (i + 1) + " employees...");
                System.out.flush();
                log.info("Seeded {} employees so far...", i + 1);
            }
        }

        // Save any remaining records
        if (!employeesBatch.isEmpty()) {
            employeeRepository.saveAll(employeesBatch);
        }

        // ✅ Write passwords to JSON file for pytest
        //File outputFile = new File(userDetailsPath);
        //File parentDir = outputFile.getParentFile();
        //if (!parentDir.exists() && !parentDir.mkdirs()) {
          //  throw new IllegalStateException("Failed to create directory: " + parentDir);
        //}
        System.out.println("Writing user details JSON to: " + new File(userDetailsPath).getAbsolutePath());
        System.out.flush();
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(userDetailsPath), emailPasswordMap);
        log.info("User details written to {}", userDetailsPath);

        System.out.println("Seeder JSON written at: " + userDetailsPath);
        System.out.println("Successfully seeded " + EMPLOYEE_COUNT + " employees!");
        System.out.flush();
        log.info("Successfully seeded {} employees!", EMPLOYEE_COUNT);
        log.info("Generated emails: {}", generatedEmails.size());
    }

    private String generateUniqueEmployeeId() {
       return "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateRandomRole() {
        List<String> roles = List.of("ROLE_EMPLOYEE", "ROLE_MANAGER", "ROLE_ADMIN");
        return roles.get(random.nextInt(roles.size()));
    }

}

