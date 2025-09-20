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
import java.util.concurrent.ConcurrentHashMap;


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

    private static final Random random = new Random();
    private static final Set<String> generatedEmails = Collections.synchronizedSet(new HashSet<>());
    private static final Set<String> generatedPhoneNumbers = Collections.synchronizedSet(new HashSet<>());
    private static final Set<String> generatedUserIds = Collections.synchronizedSet(new HashSet<>());
    private static final Map<String, Object> emailPasswordMap = new ConcurrentHashMap<>();

    @Value("${app.seed-employees:false}")
    private boolean seedEmployees;

    @Value("${user.details.path:src/test/resources/user_details.json}")
    private String userDetailsPath;

    @Value("${app.seed-employee-count:645}")
    private int employeeCount;

    @Value("${app.seed-batch-size:150}")
    private int batchSize;

    private int lastLoggedPercent = 0;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!seedEmployees) {
            log.info("Employee seeding skipped (app.seed-employees=false)");
            return;
        }

        resetDatabase();

        log.info("Seeding {} employees...", employeeCount);

        File outputFile = new File(userDetailsPath);
        if (outputFile.exists()) {
            Files.delete(outputFile.toPath());
        }

        List<User> usersBatch = new ArrayList<>();

        for (int i = 0; i < employeeCount; i++) {
            try {
                Employee employee = EmployeeGenerator.createRandomEmployee();

                String email = ensureUniqueEmail(employee.getEmail());
                String phone = ensureUniquePhone(employee.getPhoneNumber());
                String userId = ensureUniqueUserId(generateUniqueEmployeeId());

                employee.setEmail(email);
                employee.setPhoneNumber(phone);

                // Use fast password encoder in test profile
                String plainPassword = EmployeeGenerator.generateValidPassword();
                String encodedPassword = passwordEncoder.encode(plainPassword);
                User user = createUser(userId, email, encodedPassword);

                String randomRole = generateRandomRole();
                Role role = createRole(user, randomRole);
                user.setRole(role);

                employee.setUser(user);
                user.setEmployee(employee);

                emailPasswordMap.put(userId, createEmployeeDetails(email, plainPassword, randomRole));
                usersBatch.add(user);

                if (usersBatch.size() >= batchSize) {
                    userRepository.saveAll(usersBatch);
                    usersBatch.clear();
                    showProgressBar(i);
                }

            } catch (Exception e) {
                log.error("Seeder failed at iteration {}: {}", i, e.getMessage(), e);
            }
        }

        if (!usersBatch.isEmpty()) {
            userRepository.saveAll(usersBatch);
        }

        writeToJsonFile();
    }

    private void writeToJsonFile() throws IOException {
        new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValue(new File(userDetailsPath), EmployeeSeeder.emailPasswordMap);
        log.info("✅ User details JSON generated at {}", userDetailsPath);
    }

    private void resetDatabase() {
        log.info("Clearing existing data...");
        employeeRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();
        log.info("Database cleared!");
    }

    private void showProgressBar(int i) {
        int percent = ((i + 1) * 100) / employeeCount;
        if (percent != lastLoggedPercent || i == employeeCount - 1) {
            lastLoggedPercent = percent;
            if (percent % 10 == 0) {
                log.info("Progress: {}%", percent);
            }
        }
    }

    private String generateUniqueEmployeeId() {
        return "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateRandomRole() {
        List<String> roles = List.of("ROLE_EMPLOYEE", "ROLE_MANAGER", "ROLE_ADMIN");
        return roles.get(random.nextInt(roles.size()));
    }

    private Map<String, String> createEmployeeDetails(String email, String password, String role) {
        Map<String, String> details = new HashMap<>();
        details.put("email", email);
        details.put("password", password);
        details.put("role", role);
        return details;
    }

    private User createUser(String userId, String email, String encodedPassword) {
        User user = new User();
        user.setUserId(userId);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setActive(true);
        return user;
    }

    private Role createRole(User user, String roleName) {
        Role role = new Role();
        role.setUser(user);
        role.setUserRole(roleName);
        return role;
    }

    private String ensureUniqueEmail(String email) {
        while (generatedEmails.contains(email)) {
            email = email.replaceFirst("@", String.format("0%d@", random.nextInt(1, 9)));
        }
        generatedEmails.add(email);
        return email;
    }

    private String ensureUniquePhone(String phone) {
        while (generatedPhoneNumbers.contains(phone)) {
            phone = EmployeeGenerator.generateUniquePhoneNumber();
        }
        generatedPhoneNumbers.add(phone);
        return phone;
    }

    private String ensureUniqueUserId(String userId) {
        while (generatedUserIds.contains(userId)) {
            userId = generateUniqueEmployeeId();
        }
        generatedUserIds.add(userId);
        return userId;
    }
}


