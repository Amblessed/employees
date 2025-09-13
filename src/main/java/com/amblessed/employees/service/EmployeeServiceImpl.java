package com.amblessed.employees.service;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 09-Sep-25
 */

import com.amblessed.employees.entity.Employee;
import com.amblessed.employees.entity.EmployeeRequest;
import com.amblessed.employees.exception.EmployeeNotFoundException;
import com.amblessed.employees.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{

    private final EmployeeRepository employeeRepository;

    @Override
    public List<EmployeeRequest> findAll() {
        List<Employee> employees = employeeRepository.findAll();
        return employees
                .stream()
                .map(this::convertToEmployeeRequest)
                .toList();
    }

    @Override
    public EmployeeRequest findById(long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
        return convertToEmployeeRequest(employee);
    }

    @Override
    public EmployeeRequest findByEmail(String email) {
        return null;
    }

    @Override
    public List<EmployeeRequest> findByFirstName(String firstName) {
        return List.of();
    }

    @Override
    public List<EmployeeRequest> findByLastName(String lastName) {
        return List.of();
    }

    @Override
    public List<EmployeeRequest> findByDepartment(String department) {
        List<Employee> employees = employeeRepository.findEmployeeByDepartment(department);
        List<EmployeeRequest> employeeRequests = new ArrayList<>();
        for(Employee employee : employees) {
            employeeRequests.add(convertToEmployeeRequest(employee));
        }
        return employeeRequests;
    }

    @Override
    public List<EmployeeRequest> findByPosition(String position) {
        return List.of();
    }

    @Override
    public List<EmployeeRequest> findByHireDate(LocalDate hireDate) {
        return List.of();
    }

    @Override
    public List<EmployeeRequest> findByActive(Boolean active) {
        return List.of();
    }

    @Override
    public EmployeeRequest save(EmployeeRequest employeeRequest) {
        Employee employee = convertToEmployee(employeeRequest);
        return convertToEmployeeRequest(employeeRepository.save(employee));
    }

    @Override
    public EmployeeRequest update(long id, EmployeeRequest employeeRequest) {
        employeeRepository.findById(id);
        Employee employee = convertToEmployee(employeeRequest);
        employee.setId(id);
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToEmployeeRequest(savedEmployee);
    }

    @Override
    public EmployeeRequest deleteById(long employeeId) {
        EmployeeRequest employeeRequest = findById(employeeId);
        employeeRepository.deleteById(employeeId);
        return employeeRequest;
    }


    Employee convertToEmployee(EmployeeRequest employeeRequest) {
        Employee employee = new Employee();
        employee.setFirstName(employeeRequest.getFirstName());
        employee.setLastName(employeeRequest.getLastName());
        employee.setEmail(employeeRequest.getEmail());
        employee.setDepartment(employeeRequest.getDepartment());
        employee.setPosition(employeeRequest.getPosition());
        employee.setHireDate(employeeRequest.getHireDate());
        employee.setPerformanceReview(employeeRequest.getPerformanceReview());
        employee.setSkills(employeeRequest.getSkills());
        employee.setActive(employeeRequest.getActive());
        return employee;
    }

    EmployeeRequest convertToEmployeeRequest(Employee employee) {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setFirstName(employee.getFirstName());
        employeeRequest.setLastName(employee.getLastName());
        employeeRequest.setEmail(employee.getEmail());
        return employeeRequest;
    }
}
