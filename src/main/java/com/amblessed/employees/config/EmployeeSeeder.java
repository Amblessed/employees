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

    private static final int EMPLOYEE_COUNT = 645;
    static Set<String> generatedEmails = new HashSet<>();
    static Set<String> generatedPhoneNumbers = new HashSet<>();
    static Set<String> generatedUserIds = new HashSet<>();
    static Map<String, Object> emailPasswordMap = new HashMap<>();

    @Value("${app.seed-employees:false}")
    private boolean seedEmployees;

    @Value("${user.details.path:src/test/resources/user_details.json}")
    private String userDetailsPath;

    private static final int BATCH_SIZE = 150; // batch save size
    int lastLoggedPercent = 0;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        long start = System.currentTimeMillis();

        if (!seedEmployees) {
            log.info("Employee seeding skipped (app.seed-employees=false)");
            return; // skip seeding if property is false
        }

        // Clear existing data
       resetDatabase();

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


        for (int i = 0; i < EMPLOYEE_COUNT; i++) {
            try {
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
                    int percent = ((i + 1) * 100) / EMPLOYEE_COUNT;
                    log.info("✅ Batch saved at iteration {} ({}%)", i + 1, percent);
                }
                showProgresBar(i);

            } catch (Exception e) {
                log.error("❌ Seeder failed at iteration: {}", e.getMessage(), e);
            }
        }

        // Save any remaining records
        if (!usersBatch.isEmpty()) {
            userRepository.saveAll(usersBatch);
        }

        // ✅ Write passwords to JSON file for pytest
        writeToJsonFile(start, emailPasswordMap);
    }

    private void writeToJsonFile(long startTime, Map<String, Object> emailPasswordMap) throws IOException {
        log.info("Writing user details to {}", userDetailsPath);
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(userDetailsPath), emailPasswordMap);
        log.info("User details written to {}", userDetailsPath);
        log.info("Successfully seeded {} employees!", EMPLOYEE_COUNT);
        long duration = System.currentTimeMillis() - startTime;
        log.info("⏱️ Seeding completed in {} ms", duration);
    }

    private void resetDatabase(){
        log.info("Clearing existing data...");
        employeeRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();
        log.info("Cleared previous data!");
    }

    private void showProgresBar(int i){
        // Progress log every 100 employees or at final record
        int percent = ((i + 1) * 100) / EMPLOYEE_COUNT;

        // Only update if percent changed or last employee
        if (percent != lastLoggedPercent || i == EMPLOYEE_COUNT - 1) {
            int filledLength = percent / 5; // 20 chars total
            String bar = "=".repeat(filledLength) + " ".repeat(20 - filledLength);

            // Print bar, percent, and exact count
            if (percent % 10 == 0) {
                log.info("Progress [{}] {}% ({} of {})", bar, percent, (i + 1), EMPLOYEE_COUNT);
            }
            lastLoggedPercent = percent;
        }
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
            email = email.replaceFirst("@", String.format("0%d@", random.nextInt(1,9)));
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

