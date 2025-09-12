package com.amblessed.employees.config;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 12-Sep-25
 */



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

public class EmployeeDateGenerator {

    private EmployeeDateGenerator(){
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static EmployeeDates generateDates() {
        LocalDate hireDate = getRandomHireDate();
        LocalDateTime createdAt = hireDate.atTime(randomInt(0,24), randomInt(0,60), randomInt(0,60));
        long totalSeconds = randomInt(1, 366*24*3600);
        LocalDateTime updatedAt = createdAt.plusSeconds(totalSeconds);
        if (updatedAt.isAfter(LocalDateTime.now())) updatedAt = LocalDateTime.now();
        return new EmployeeDates(hireDate, createdAt, updatedAt);
    }

    private static LocalDate getRandomHireDate() {
        LocalDate start = LocalDate.of(2010,1,1);
        LocalDate end = LocalDate.now();
        return LocalDate.ofEpochDay(ThreadLocalRandom.current().nextLong(start.toEpochDay(), end.toEpochDay() + 1));
    }

    public static int randomInt(int minInclusive, int maxExclusive) {
        return ThreadLocalRandom.current().nextInt(minInclusive, maxExclusive);
    }
}
