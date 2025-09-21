package com.amblessed.employees.config;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 12-Sep-25
 */


import com.amblessed.employees.entity.Employee;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;


import static com.amblessed.employees.config.EmployeeDateGenerator.randomInt;

public class EmployeeGenerator {


    private static final Faker faker = new Faker();
    private static final Random random = new Random();


    private EmployeeGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }


    public static Employee createRandomEmployee() {
        String department = DepartmentService.getRandomDepartment();
        String position = DepartmentService.getRandomPosition(department);
        List<String> skillsPool = DepartmentService.getSkillsForPosition(position);

        EmployeeDates dates = EmployeeDateGenerator.generateDates();
        String skills = String.join(", ", DepartmentService.getRandomSkills(skillsPool));

        Employee employee = new Employee();
        String firstName = faker.name().firstName();
        String lastName = faker.name().fullName().split(" ")[1];


        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(generateUniqueEmail(firstName, lastName, department));
        employee.setPhoneNumber(generateUniquePhoneNumber());
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setSalary(BigDecimal.valueOf(calculateSalary(position)));
        employee.setHireDate(dates.hireDate());
        employee.setPerformanceReview(PerformanceReviewGenerator.generateReview(department));
        employee.setSkills(skills);
        employee.setCreatedAt(dates.createdAt());
        employee.setUpdatedAt(dates.updatedAt());
        employee.setActive(faker.bool().bool());
        return employee;
    }

    // Generate unique email
    private static String generateUniqueEmail(String firstName, String lastName, String department) {
        firstName = firstName.toLowerCase().replace("'", "");
        lastName = lastName.toLowerCase().replace("'", "");
        department = department.toLowerCase().replace(" ", "_");
        return String.format("%s.%s@%s.amblessed.com", firstName, lastName, department);
    }

    // Generate unique phone number
    public static String generateUniquePhoneNumber() {
        int areaCode = 200 + random.nextInt(800);       // 200-999
        int centralOfficeCode = 200 + random.nextInt(800); // 200-999
        int lineNumber = 1000 + random.nextInt(9000);   // 1000-9999
        return String.format("(%03d) %03d-%04d", areaCode, centralOfficeCode, lineNumber);
    }

    private static int calculateSalary(String position) {
        return switch (position) {
            case "Senior Software Engineer", "Lead Developer", "Manager" -> randomInt(100000, 150000);
            case "Software Engineer", "Data Scientist", "Data Engineer", "DevOps Engineer" -> randomInt(90000, 120000);
            case "Junior Developer", "Data Analyst" -> randomInt(70000, 90000);
            default -> randomInt(50000, 80000);
        };

    }

    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        if (password.length() < 8) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*\\d.*")) return false;
        return password.matches(".*[!@#$%^&*()].*");
    }

    public static String generateValidPassword() {
        String password = faker.internet().password(12,16, true, true, true);
        while (!isValidPassword(password)) {
            password = faker.internet().password(12,16, true, true, true);
        }
        return password;
    }



}
