package com.amblessed.employees.repository;

import com.amblessed.employees.entity.Employee;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EnabledForJreRange(min = JRE.JAVA_17)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeRepositoryTests {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        // Create a single employee for individual tests
        employee = new EmployeeBuilder()
                .withUserId("EMP-00567")
                .withEmail("email@domain.com")
                .withFirstName("Okechukwu")
                .withLastName("Bright")
                .withDepartment("Sales")
                .withPosition("Manager")
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("Injected components are not null")
    void injectedComponentAreNotNull() {
        assertThat(employeeRepository).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("Save employee and verify saved employee")
    void givenEmployeeObject_whenSave_thenReturnSavedEmployee() {
        Employee savedEmployee = employeeRepository.save(employee);

        assertNotNull(savedEmployee);
        assertTrue(savedEmployee.getId() > 0);
        assertEquals("EMP-00567", savedEmployee.getUser().getUserId());
    }

    @Test
    @Order(3)
    @DisplayName("Find employee by email returns correct employee")
    void givenEmployeeEmail_whenFindByEmail_thenReturnEmployee() {
        employeeRepository.save(employee);

        Optional<Employee> foundEmployee = employeeRepository.findByEmail(employee.getEmail());

        assertThat(foundEmployee).isPresent();
        assertThat(foundEmployee.get().getEmail()).isEqualTo(employee.getEmail());
        assertThat(foundEmployee.get().getFirstName()).isEqualTo(employee.getFirstName());
    }

    @Test
    @Order(4)
    @DisplayName("Find by invalid email returns empty")
    void givenInvalidEmployeeEmail_whenFindByEmail_thenReturnEmptyOptional() {
        employeeRepository.save(employee);

        Optional<Employee> foundEmployee = employeeRepository.findByEmail("invalid.email@amblessed.com");

        assertTrue(foundEmployee.isEmpty());
    }

    @Test
    @Order(5)
    @DisplayName("Find employees by department returns employees in that department")
    void givenDepartment_whenFindEmployeeByDepartment_thenReturnEmployees() {
        // Generate 50 employees, mostly in Engineering dept
        List<Employee> employees = EmployeeFactory.createEmployees(50);
        employeeRepository.saveAll(employees);

        String department = "Engineering";
        List<Employee> foundEmployees = employeeRepository.findEmployeeByDepartment(department);

        assertThat(foundEmployees).isNotEmpty()
                .allMatch(emp -> emp.getDepartment().equals(department));
    }

    @Test
    @Order(6)
    @DisplayName("Find by invalid department returns empty list")
    void givenInvalidDepartment_whenFindEmployeeByDepartment_thenReturnEmptyList() {
        List<Employee> employees = EmployeeFactory.createEmployees(30);
        employeeRepository.saveAll(employees);

        List<Employee> foundEmployees = employeeRepository.findEmployeeByDepartment("Invalid Department");

        assertThat(foundEmployees).isEmpty();
    }

    @Test
    @Order(7)
    @DisplayName("Find employees by position returns employees in that position")
    void givenPosition_whenFindEmployeeByPosition_thenReturnEmployees() {
        List<Employee> employees = EmployeeFactory.createEmployees(40);
        employeeRepository.saveAll(employees);

        String position = "Developer";
        List<Employee> foundEmployees = employeeRepository.findEmployeeByPosition(position);

        assertThat(foundEmployees).isNotEmpty()
                .allMatch(emp -> emp.getPosition().equals(position));
    }

    @Test
    @Order(8)
    @DisplayName("Find by invalid position returns empty list")
    void givenInvalidPosition_whenFindEmployeeByPosition_thenReturnEmptyList() {
        List<Employee> employees = EmployeeFactory.createEmployees(20);
        employeeRepository.saveAll(employees);

        List<Employee> foundEmployees = employeeRepository.findEmployeeByPosition("Invalid Position");

        assertThat(foundEmployees).isEmpty();
    }

    @Test
    @Order(9)
    @DisplayName("Find employees by firstName and lastName")
    void givenFirstNameAndLastName_whenFindByFirstNameAndLastName_thenReturnEmployees() {
        employeeRepository.save(employee);

        Employee anotherEmployee = new EmployeeBuilder()
                .withFirstName(employee.getFirstName())
                .withLastName(employee.getLastName())
                .withUserId("EMP-00999")
                .withEmail("duplicate.name@example.com")
                .build();

        employeeRepository.save(anotherEmployee);

        List<Employee> foundEmployees = employeeRepository.findByFirstNameAndLastName(employee.getFirstName(), employee.getLastName());

        assertThat(foundEmployees).hasSize(2)
                .allMatch(emp -> emp.getFirstName().equals(employee.getFirstName()))
                .allMatch(emp -> emp.getLastName().equals(employee.getLastName()));
    }

    @Test
    @Order(10)
    @DisplayName("Find employees by firstName or lastName")
    void givenFirstNameOrLastName_whenFindByFirstNameOrLastName_thenReturnEmployees() {
        Employee emp1 = new EmployeeBuilder()
                .withFirstName("UniqueFirstName")
                .withUserId("EMP-00100")
                .withEmail("unique1@example.com")
                .build();

        Employee emp2 = new EmployeeBuilder()
                .withLastName("UniqueLastName")
                .withUserId("EMP-00101")
                .withEmail("unique2@example.com")
                .build();

        employeeRepository.saveAll(List.of(emp1, emp2));

        List<Employee> foundEmployees = employeeRepository.findByFirstNameOrLastName("UniqueFirstName", "UniqueLastName");

        assertThat(foundEmployees).hasSize(2)
                .anyMatch(emp -> emp.getFirstName().equals("UniqueFirstName"))
                .anyMatch(emp -> emp.getLastName().equals("UniqueLastName"));
    }

    @Test
    @Order(11)
    @DisplayName("Find all employees returns full list")
    void givenEmployeeList_whenFindAll_thenReturnEmployeeList() {
        List<Employee> employees = EmployeeFactory.createEmployees(100);
        employeeRepository.saveAll(employees);

        List<Employee> foundEmployees = employeeRepository.findAll();

        assertThat(foundEmployees).hasSize(100);
    }
}
