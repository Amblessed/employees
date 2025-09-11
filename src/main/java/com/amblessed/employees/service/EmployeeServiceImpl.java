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
import org.springframework.transaction.annotation.Transactional;

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
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setFirstName(employee.getFirstName());
        employeeRequest.setLastName(employee.getLastName());
        employeeRequest.setEmail(employee.getEmail());
        return employeeRequest;
    }

    @Override
    @Transactional
    public EmployeeRequest save(EmployeeRequest employeeRequest) {
        Employee employee = convertToEmployee(employeeRequest);
        return convertToEmployeeRequest(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public EmployeeRequest update(long id, EmployeeRequest employeeRequest) {
        employeeRepository.findById(id);
        Employee employee = convertToEmployee(employeeRequest);
        employee.setId(id);
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToEmployeeRequest(savedEmployee);
    }

    @Override
    @Transactional
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
