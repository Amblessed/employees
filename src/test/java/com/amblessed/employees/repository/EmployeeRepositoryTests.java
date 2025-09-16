package com.amblessed.employees.repository;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 12-Sep-25
 */



import com.amblessed.employees.config.DepartmentService;
import com.amblessed.employees.config.EmployeeGenerator;
import com.amblessed.employees.entity.Employee;
import com.amblessed.employees.entity.Role;
import com.amblessed.employees.entity.User;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    private Employee employee;
    private List<Employee> employees;
    private static final int EMPLOYEE_COUNT = 500;


    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();  // This is to ensure the in-memory database is empty before each test
        roleRepository.deleteAll();
        userRepository.deleteAll();
        User user = createUser(); // assumes user is already saved or will be saved separately
        userRepository.save(user);
        employee = EmployeeGenerator.createRandomEmployee();
        employee.setUser(user);

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


        // Act
        Employee savedEmployee = employeeRepository.save(employee);

        // Assert
        assertNotNull(savedEmployee);
        assertTrue(savedEmployee.getId() > 0);
        assertEquals("EMP-00567", savedEmployee.getUser().getUserId());

    }

    @Test
    @Order(3)
    @DisplayName("Find employee by email returns the correct employee")
    void givenEmployeeEmail_whenFindByEmail_thenReturnEmployee() {

        // Act
        Employee savedEmployee = employeeRepository.save(employee);
        Optional<Employee> foundEmployee = employeeRepository.findByEmail(savedEmployee.getEmail());

        // assert
        assertThat(foundEmployee).isPresent();
        assertThat(foundEmployee.get().getEmail()).isEqualTo(savedEmployee.getEmail());
        assertThat(foundEmployee.get().getFirstName()).isEqualTo(savedEmployee.getFirstName());
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
        assertThat(savedEmployees).isNotEmpty().hasSize(EMPLOYEE_COUNT);
    }


    private List<Employee> generateRandomEmployees(int count) {
       List<Employee> emps = new ArrayList<>();
       for (int i = 0; i < count; i++) {
           User user = new User();
           user.setUserId("EMP-00" + i);
           user.setPassword("password");
           user.setEmail("email@domain.com");
           user.setActive(true);
           Role role = new Role();
           role.setUserRole("ROLE_ADMIN");
           role.setUser(user);
           user.setRole(role);
           userRepository.save(user);
           Employee emp = new Employee();
           emp.setUser(user);
           emps.add(emp);
       }
       return emps;
    }

    private User createUser() {
        User user = new User();
        user.setUserId("EMP-00567");
        user.setPassword("password");
        user.setEmail("email@domain.com");
        user.setActive(true);

        Role role = new Role();
        role.setUserRole("ROLE_ADMIN");
        role.setUser(user);
        user.setRole(role);

        return user;
    }

}
