package com.amblessed.employees.config;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 12-Sep-25
 */



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


public class DepartmentService {

    private DepartmentService() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final Map<String, List<String>> DEPARTMENTS = Map.ofEntries(
            Map.entry("Engineering", List.of("Software Engineer", "Senior Software Engineer", "Lead Developer", "DevOps Engineer", "Test Analyst", "Test Automation Engineer")),
            Map.entry("Product", List.of("Product Manager", "Product Owner", "Product Designer")),
            Map.entry("Analytics", List.of("Data Analyst", "Data Scientist", "Machine Learning Engineer", "Data Engineer")),
            Map.entry("HR", List.of("HR Specialist", "HR Manager", "HR Assistant")),
            Map.entry("Finance", List.of("Finance Manager", "Accountant", "Financial Analyst")),
            Map.entry("Sales", List.of("Sales Executive", "Sales Manager", "Sales Assistant")),
            Map.entry("IT Support", List.of("IT Support Engineer", "IT Support Manager", "IT Support Specialist")),
            Map.entry("Operations", List.of("Operations Coordinator", "Operations Manager", "Operations Analyst"))
    );


    private static final Map<String, List<String>> SKILLS_BY_POSITION = Map.ofEntries(
            Map.entry("Software Engineer", List.of("Java", "Spring Boot", "PostgreSQL", "Python", "Docker", "REST APIs", "Javascript")),
            Map.entry("Senior Software Engineer", List.of("Java", "Spring Boot", "Microservices", "Kubernetes", "System Design", "CI/CD", "Code Reviews", "Code Quality", "Code Optimization", "Software Architecture")),
            Map.entry("Lead Developer", List.of("Java", "System Architecture", "Project Management", "Agile", "Team Leadership")),
            Map.entry("DevOps Engineer", List.of("Docker", "Kubernetes", "CI/CD", "AWS", "Linux", "Automation")),
            Map.entry("Test Analyst", List.of("Java", "Selenium", "Python", "SQL", "TestNG", "REST APIs", "JMeter", "Maven", "CI/CD", "Manual Testing", "Test Cases Creation", "Test Planning")),
            Map.entry("Test Automation Engineer", List.of("Java", "Selenium", "Python", "SQL", "TestNG", "REST APIs", "JMeter", "Maven", "CI/CD", "Scripting")),
            Map.entry("Product Manager", List.of("Agile", "Scrum", "Leadership", "Communication", "Roadmapping", "User Stories")),
            Map.entry("Product Owner", List.of("Agile", "Scrum", "Leadership", "Communication", "Roadmapping", "User Stories")),
            Map.entry("Product Designer", List.of("Agile", "Scrum", "Leadership", "Communication", "Roadmapping", "User Stories", "UX/UI Design", "Wireframes", "Prototypes")),
            Map.entry("Data Analyst", List.of("SQL", "Python", "Pandas", "Excel", "Data Visualization", "Power BI", "Tableau")),
            Map.entry("Data Scientist", List.of("Python", "Machine Learning", "TensorFlow", "Pandas", "Statistics", "R")),
            Map.entry("Machine Learning Engineer", List.of("Python", "Machine Learning", "TensorFlow", "Pandas", "Statistics", "R",  "Data Visualization", "Power BI", "Tableau")),
            Map.entry("Data Engineer", List.of("Python", "ETL", "SQL", "Pandas", "Data Visualization", "Power BI", "Tableau", "Airflow", "Docker", "CI/CD")),
            Map.entry("HR Specialist", List.of("Recruitment", "Onboarding", "Conflict Resolution", "Communication", "HR Policies")),
            Map.entry("HR Manager", List.of("HR Strategy", "Leadership", "Employee Engagement", "Conflict Management")),
            Map.entry("HR Assistant", List.of("HR Strategy", "Leadership", "Employee Engagement", "Conflict Management")),
            Map.entry("Finance Manager", List.of("Accounting", "Budgeting", "Excel", "Risk Management", "Financial Analysis", "Forecasting")),
            Map.entry("Accountant", List.of("Accounting", "Excel", "Taxation", "Auditing", "Financial Reporting")),
            Map.entry("Financial Analyst", List.of("Accounting", "Excel", "Taxation", "Auditing", "Financial Reporting", "Risk Management", "Forecasting", "Financial Analysis")),
            Map.entry("Sales Executive", List.of("Negotiation", "CRM", "Lead Generation", "Networking", "Closing Deals", "Customer Relations")),
            Map.entry("Sales Manager", List.of("Sales Strategy", "Team Leadership", "Target Planning", "CRM", "Negotiation", "Customer Relations", "Networking", "Closing Deals", "Lead Generation")),
            Map.entry("Sales Assistant", List.of("Sales Strategy", "Agile", "Target Planning", "CRM", "Negotiation", "Customer Relations", "Networking")),
            Map.entry("IT Support Engineer", List.of("Troubleshooting", "Networking", "Windows", "Linux", "Customer Support")),
            Map.entry("IT Support Manager", List.of("Troubleshooting", "Networking", "Windows", "Linux", "Customer Support", "Team Leadership", "Project Management", "Agile", "Communication")),
            Map.entry("IT Support Specialist", List.of("Troubleshooting", "Networking", "Windows", "Linux", "Customer Support", "Agile", "Problem Solving", "Customer Support")),
            Map.entry("Operations Coordinator", List.of("Logistics", "Scheduling", "Process Management", "Communication")),
            Map.entry("Operations Analyst", List.of("Logistics", "Scheduling", "Process Management", "Communication")),
            Map.entry("Operations Manager", List.of("Leadership", "Process Optimization", "Project Management", "Budgeting"))
    );

    public static final Map<String, String[]> STRENGTHS_BY_DEPARTMENT = Map.of(
            "Engineering", new String[] {
                    "excellent coding skills",
                    "strong problem-solving abilities",
                    "good understanding of system architecture",
                    "proactive in code reviews",
                    "quickly adapts to new technologies"
            },
            "Product", new String[] {
                    "strong product vision",
                    "excellent communication with stakeholders",
                    "good prioritization skills",
                    "creates clear roadmaps",
                    "leads cross-functional teams effectively"
            },
            "Analytics", new String[] {
                    "strong data analysis skills",
                    "excellent in statistical modeling",
                    "able to derive actionable insights",
                    "proficient with data visualization tools",
                    "good at predictive modeling"
            },
            "HR", new String[] {
                    "excellent employee engagement skills",
                    "strong recruitment capabilities",
                    "great conflict resolution",
                    "effective communication",
                    "leads training sessions successfully"
            },
            "Finance", new String[] {
                    "accurate financial analysis",
                    "strong budgeting skills",
                    "attention to detail",
                    "effective risk management",
                    "good forecasting abilities"
            },
            "Sales", new String[] {
                    "excellent client relationship management",
                    "strong negotiation skills",
                    "consistently meets sales targets",
                    "great at lead generation",
                    "persuasive communication"
            },
            "IT Support", new String[] {
                    "quick troubleshooting abilities",
                    "strong customer support skills",
                    "good knowledge of networks and systems",
                    "responsive to tickets",
                    "resolves issues efficiently"
            },
            "Operations", new String[] {
                    "excellent process management",
                    "strong organizational skills",
                    "effective resource allocation",
                    "leads teams efficiently",
                    "optimizes workflows successfully"
            }
    );

    private static final List<String> departmentKeys = new ArrayList<>(DEPARTMENTS.keySet());

    public static String getRandomDepartment() {
        return departmentKeys.get(ThreadLocalRandom.current().nextInt(departmentKeys.size()));
    }

    public static String getRandomPosition(String department) {
        List<String> positions = DEPARTMENTS.get(department);
        return positions.get(ThreadLocalRandom.current().nextInt(positions.size()));
    }

    public static List<String> getSkillsForPosition(String position) {
        return new ArrayList<>(SKILLS_BY_POSITION.getOrDefault(position, List.of()));
    }

    public static List<String> getRandomSkills(List<String> skillsPool) {
        Collections.shuffle(skillsPool);
        return skillsPool.subList(0, ThreadLocalRandom.current().nextInt(2, skillsPool.size() + 1));
    }
}
