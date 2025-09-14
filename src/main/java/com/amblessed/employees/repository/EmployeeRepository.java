package com.amblessed.employees.repository;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 10-Sep-25
 */


import com.amblessed.employees.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByEmail(String email);
    List<Employee> findByFirstName(String firstName);
    List<Employee> findByLastName(String lastName);
    List<Employee> findByFirstNameAndLastName(String firstName, String lastName);
    List<Employee> findEmployeeByDepartment(String department);
    List<Employee> findEmployeeByPosition(String position);
    List<Employee> findByFirstNameOrLastName(String firstName, String lastName);
    Optional<Employee> findByEmployeeId(String employeeId);
    void deleteByEmployeeId(String employeeId);
    boolean existsByEmployeeId(String userId);



    // define a custom query using JPQL with named params

    @Query("SELECT e FROM Employee e WHERE e.firstName = :firstName and e.lastName = :lastName")
    Optional<Employee> findByFirstNameAndLastNameNamedParams(@Param("firstName")String firstName, @Param("lastName")String lastName);


    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
