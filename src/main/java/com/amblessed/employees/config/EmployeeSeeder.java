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
import java.io.IOException;
import java.nio.file.Files;
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

    private static final int EMPLOYEE_COUNT = 1000;
    static Set<String> generatedEmails = new HashSet<>();
    static Set<String> generatedPhoneNumbers = new HashSet<>();
    static Set<String> generatedUserIds = new HashSet<>();
    static Map<String, Object> emailPasswordMap = new HashMap<>();

    @Value("${app.seed-employees:false}")
    private boolean seedEmployees;

    @Value("${user.details.path:src/test/resources/user_details.json}")
    private String userDetailsPath;

    private static final int BATCH_SIZE = 250; // batch save size

    @Override
    @Transactional
    public void run(String... args)  throws Exception {

        if (!seedEmployees){
            log.info("Employee seeding skipped (app.seed-employees=false)");
            return; // skip seeding if property is false
        }

        // Clear existing data
        log.info("Clearing existing data...");
        employeeRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();


        log.info("Cleared previous data!");

        log.info("Seeding {} employees...", EMPLOYEE_COUNT);
        File outputFile = new File(userDetailsPath);
        log.info("User details path: {}", userDetailsPath);
        if (outputFile.exists()) {
            log.info("User details file exists, deleting...");
            try {
                Files.delete(outputFile.toPath());
            } catch (IOException e) {
                log.warn("Failed to delete existing file: {}", outputFile.getAbsolutePath(), e);
            }
        }

        List<User> usersBatch = new ArrayList<>();

        // store plain-text passwords

        for (int i = 0; i < EMPLOYEE_COUNT; i++) {
            log.info("Starting employee iteration {}", i + 1);
            Employee employee = EmployeeGenerator.createRandomEmployee();
            String email = ensureUniqueEmail(employee.getEmail(), generatedEmails);
            String phoneNumber = ensureUniquePhone(employee.getPhoneNumber(), generatedPhoneNumbers);
            String userId = ensureUniqueUserId(generateUniqueEmployeeId(), generatedUserIds);

            employee.setEmail(email);
            employee.setPhoneNumber(phoneNumber);


            // Generate User
            String plainPassword = EmployeeGenerator.generateValidPassword();
            String encodedPassword = passwordEncoder.encode(plainPassword);
            User user = createUser(userId, email, encodedPassword);

            // Generate Role
            String randomRole = generateRandomRole();
            Role createdRole = createRole(user, randomRole);

            // Link user ↔ role
            user.setRole(createdRole);

            // Link employee ↔ user
            employee.setUser(user);
            user.setEmployee(employee);

            // Save mapping for pytest
            emailPasswordMap.put(userId, createEmployeeDetails(email, plainPassword, randomRole));
            usersBatch.add(user);

            // Save in batches
            if (usersBatch.size() >= BATCH_SIZE) {
                userRepository.saveAll(usersBatch);
                usersBatch.clear();
                //log.info("Seeded {} employees so far...", i + 1);
            }

            // Progress log every 100 employees or at final record
            if ((i + 1) % 100 == 0 || i == EMPLOYEE_COUNT - 1) {
                int percent = ((i + 1) * 100) / EMPLOYEE_COUNT;
                log.info("Seeding progress: {} of {} employees ({}%)", i + 1, EMPLOYEE_COUNT, percent);
            }
        }

        // Save any remaining records
        if (!usersBatch.isEmpty()) {
            userRepository.saveAll(usersBatch);
        }

        // ✅ Write passwords to JSON file for pytest
        log.info("Writing user details to {}", userDetailsPath);
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(userDetailsPath), emailPasswordMap);
        log.info("User details written to {}", userDetailsPath);
        log.info("Successfully seeded {} employees!", EMPLOYEE_COUNT);
    }

    private String generateUniqueEmployeeId() {
       return "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateRandomRole() {
        List<String> roles = List.of("ROLE_EMPLOYEE", "ROLE_MANAGER", "ROLE_ADMIN");
        return roles.get(random.nextInt(roles.size()));
    }

    private Map<String, String> createEmployeeDetails(String email, String plainPassword, String role) {
        Map<String, String> details = new HashMap<>();
        details.put("email", email);
        details.put("password", plainPassword);
        details.put("role", role);
        return details;
    }

    private User createUser(String userId, String email, String encodedPassword){
        User user = new User();
        user.setUserId(userId); // FK link
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setActive(true);
        return user;
    }

    private Role createRole(User user, String randomRole){
        Role role = new Role();
        role.setUser(user);
        role.setUserRole(randomRole);
        return role;
    }

    // --- Helper methods ---
    private String ensureUniqueEmail(String email, Set<String> existingEmails) {
        while (existingEmails.contains(email)) {
            email = email.replaceFirst("@", String.format("%d@", random.nextInt(100)));
        }
        existingEmails.add(email);
        return email;
    }

    private String ensureUniquePhone(String phone, Set<String> existingPhones) {
        while (existingPhones.contains(phone)) {
            phone = EmployeeGenerator.generateUniquePhoneNumber();
        }
        existingPhones.add(phone);
        return phone;
    }

    private String ensureUniqueUserId(String userId, Set<String> existingIds) {
        while (existingIds.contains(userId)) {
            userId = generateUniqueEmployeeId();
        }
        existingIds.add(userId);
        return userId;
    }

}

