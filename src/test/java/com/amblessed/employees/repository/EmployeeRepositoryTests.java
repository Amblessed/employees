package com.amblessed.employees.repository;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 12-Sep-25
 */



import com.amblessed.employees.config.DepartmentService;
import com.amblessed.employees.config.EmployeeGenerator;
import com.amblessed.employees.entity.Employee;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.*;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;



@DataJpaTest // This annotation is used to test JPA repositories
@EnabledForJreRange(min = JRE.JAVA_17) // This annotation is used to enable the test for Java 17 and above since we are using Spring Boot 3
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeRepositoryTests {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;
    private List<Employee> employees;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();  // This is to ensure the in-memory database is empty before each test
        employee = generateRandomEmployees(1).getFirst();
        employees = generateRandomEmployees(1000);

    }

    @Test
    @Order(1)
    @DisplayName("JUnit test for injected component are not null")
    void injectedComponentAreNotNull(){
        assertThat(employeeRepository).isNotNull();
    }


    @Test
    @Order(2)
    @DisplayName("JUnit Test for saving an employee")
    void givenEmployeeObject_whenSave_thenReturnSavedEmployee() {

        //given (or Arrange) - precondition or setup
        /* employee object already created in the set-up method*/

        //when (or Act) - action or behavior that we are going to test
        Employee savedEmployee = employeeRepository.save(employee);

        //then (or Assert) - the expected result
        assertTrue(savedEmployee.getId() > 0);
        assertThat(savedEmployee.getId()).isPositive();
        assertThat(savedEmployee).isNotNull();
        assertEquals(employee, savedEmployee);
    }


    @Test
    @Order(3)
    @DisplayName("Find employee by email returns the correct employee")
    void givenEmployeeEmail_whenFindByEmail_thenReturnEmployee() {
        // arrange
        employeeRepository.save(employee);

        // act
        Optional<Employee> foundEmployee = employeeRepository.findByEmail(employee.getEmail());

        // assert
        assertThat(foundEmployee).isPresent();
        assertThat(foundEmployee.get().getEmail()).isEqualTo(employee.getEmail());
        assertThat(foundEmployee.get().getFirstName()).isEqualTo(employee.getFirstName());
    }

    @Test
    @Order(4)
    @DisplayName("Find employee by invalid email returns empty optional")
    void givenInvalidEmployeeEmail_whenFindByEmail_thenReturnEmptyOptional() {
        // arrange
        employeeRepository.save(employee);

        // act
        Optional<Employee> foundEmployee = employeeRepository.findByEmail("invalid.email@amblessed.com");

        // assert
        assertTrue(foundEmployee.isEmpty());
    }

    @Test
    @Order(5)
    @DisplayName("Find employees by department returns all employees in that department")
    void givenDepartment_whenFindEmployeeByDepartment_thenReturnEmployees() {
        // arrange
        employeeRepository.saveAll(employees);

        // act
        String department = DepartmentService.getRandomDepartment();
        System.out.println("Selected Department: " + department);
        List<Employee> foundEmployees = employeeRepository.findEmployeeByDepartment(department);

        // assert
        assertThat(foundEmployees).isNotEmpty().allMatch(emp -> emp.getDepartment().equals(department));
    }



    @Test
    @Order(6)
    @DisplayName("Find employees by invalid department returns empty list")
    void givenInvalidDepartment_whenFindEmployeeByDepartment_thenReturnEmptyList() {
        // arrange
        employeeRepository.saveAll(employees);

        // act
        List<Employee> foundEmployees = employeeRepository.findEmployeeByDepartment("Invalid Department");

        // assert
        assertThat(foundEmployees).isEmpty();
    }

    @Test
    @Order(7)
    @DisplayName("Find employees by position returns all employees in that department")
    void givenPosition_whenFindEmployeeByPosition_thenReturnEmployees() {
        // arrange
        employeeRepository.saveAll(employees);

        // act
        String department = DepartmentService.getRandomDepartment();
        String position = DepartmentService.getRandomPosition(department);
        System.out.println("Selected Department: " + department);
        System.out.println("Selected Position: " + position);
        List<Employee> foundEmployees = employeeRepository.findEmployeeByPosition(position);

        // assert
        assertThat(foundEmployees).isNotEmpty()
                .allMatch(emp -> emp.getPosition().equals(position))
                .allMatch(emp -> emp.getDepartment().equals(department));
    }

    @Test
    @Order(8)
    @DisplayName("Find employees by invalid position returns empty list")
    void givenInvalidPosition_whenFindEmployeeByPosition_thenReturnEmptyList() {
        //given (or Arrange) - precondition or setup
        employeeRepository.saveAll(employees);

        //when (or Act) - action or the behaviour that we are going test
        List<Employee> foundEmployees = employeeRepository.findEmployeeByPosition("Invalid Position");

        //then (or Assert)
        assertThat(foundEmployees).isEmpty();
    }

    @Test
    @Order(9)
    @DisplayName("Find employees by firstName and lastName returns all employees in that department with that name")
    void givenEmployeeFirstNameAndLastName_whenFindEmployeeByFirstNameAndLastName_thenReturnEmployees() {
        // arrange
        String firstName = employee.getFirstName();
        String lastName = employee.getLastName();
        employeeRepository.save(employee);
        Employee employeeSame = generateRandomEmployees(1).getFirst();
        employeeSame.setFirstName(firstName);
        employeeSame.setLastName(lastName);
        employeeRepository.save(employeeSame);

        employeeRepository.saveAll(employees);

        // act
        List<Employee> foundEmployees = employeeRepository.findByFirstNameAndLastName(firstName, lastName);

        // assert
        assertThat(foundEmployees).isNotEmpty().hasSize(2)
                    .allMatch(emp -> emp.getFirstName().equals(firstName))
                    .allMatch(emp -> emp.getLastName().equals(lastName));
    }

    @Test
    @Order(9)
    @DisplayName("Find employees by firstName or lastName returns all employees in that department with that name")
    void givenEmployeeFirstNameOrLastName_whenFindEmployeeByFirstNameAndLastName_thenReturnEmployees() {
        // arrange
        String firstName = "Okechukwu Bright";
        employee.setFirstName(firstName);
        employeeRepository.save(employee);

        Employee employeeSame = generateRandomEmployees(1).getFirst();
        employeeSame.setLastName(firstName);
        employeeRepository.save(employeeSame);

        employeeRepository.saveAll(employees);

        // act
        List<Employee> foundEmployees = employeeRepository.findByFirstNameOrLastName(firstName, firstName);

        // assert
        assertThat(foundEmployees).isNotEmpty().hasSize(2)
                .anyMatch(emp -> emp.getFirstName().equals(firstName))
                .anyMatch(emp -> emp.getLastName().equals(firstName));
    }

    @Test
    @Order(10)
    @DisplayName("JUnit Test for Verifying the Functionality of the findAll Method in employees rest api")
    void givenEmployeeList_whenFindAll_thenReturnEmployeeList(){
        //given (or Arrange) - precondition or setup
        employeeRepository.saveAll(employees);

        //when (or Act) - action or the behaviour that we are going test
        List<Employee> savedEmployees = employeeRepository.findAll();

        //then (or Assert)
        assertNotNull(savedEmployees);
        assertThat(savedEmployees).isNotEmpty().hasSize(1000);
    }


    private static List<Employee> generateRandomEmployees(int count) {
        List<Employee> employees = new ArrayList<>();
       for (int i = 0; i < count; i++) {
           employees.add(EmployeeGenerator.createRandomEmployee());
       }
       return employees;
    }

}
