package com.amblessed.employees.config;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 12-Sep-25
 */


import net.datafaker.Faker;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class PerformanceReviewGenerator {

    private PerformanceReviewGenerator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String generateReview(String department) {
        String[] strengths = DepartmentService.STRENGTHS_BY_DEPARTMENT.getOrDefault(department, new String[]{"dedicated team member"});
        String[] improvements = {"needs to improve time management", "should enhance communication skills"};
        String[] general = {"consistently meets expectations", "exceeds performance goals"};

        StringBuilder review = new StringBuilder("Strengths: " + strengths[new Random().nextInt(strengths.length)] + ". ");
        review.append("Improvements: ").append(improvements[new Random().nextInt(improvements.length)]).append(". ");
        review.append("Overall: ").append(general[new Random().nextInt(general.length)]).append(". ");

        int extra = ThreadLocalRandom.current().nextInt(1,4);
        for (int i=0;i<extra;i++) review.append("Additional feedback: ").append(new Faker().lorem().sentence()).append(" ");
        return review.toString().trim();
    }
}
