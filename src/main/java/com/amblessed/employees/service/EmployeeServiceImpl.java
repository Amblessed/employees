package com.amblessed.employees.service;


/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 09-Sep-25
 */


import com.amblessed.employees.config.EmployeeGenerator;
import com.amblessed.employees.entity.*;
import com.amblessed.employees.exception.InvalidPasswordException;
import com.amblessed.employees.mapper.EmployeeMapper;
import com.amblessed.employees.config.AppConstants;
import com.amblessed.employees.exception.ResourceNotFoundException;
import com.amblessed.employees.repository.EmployeeRepository;
import com.amblessed.employees.repository.RoleRepository;
import com.amblessed.employees.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public Page<EmployeeResponse> findAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, getSort(sortBy, sortDirection));
        Page<Employee> employees = employeeRepository.findAll(pageDetails);
        return employees.map(this::mapToResponse);
    }

    @Override
    public List<EmployeeResponse> filterEmployees(String department, String position, BigDecimal salary) {
        Specification<Employee> spec = null;
        if (department != null) {
            spec = EmployeeSpecification.hasDepartment(department);
        }
        if (position != null) {
            spec = (spec == null) ? EmployeeSpecification.hasPosition(position)
                    : spec.and(EmployeeSpecification.hasPosition(position));
        }
        if (salary != null) {
            spec = (spec == null) ? EmployeeSpecification.hasSalary(salary)
                    : spec.and(EmployeeSpecification.hasSalary(salary));
        }
        Pageable pageDetails = PageRequest.of(0, 20, getSort(AppConstants.SORT_BY_FIRSTNAME, AppConstants.SORT_DIRECTION));
        Page<Employee> employees = (spec == null) ? employeeRepository.findAll(pageDetails) : employeeRepository.findAll(spec, pageDetails);
        return employees.stream().map(this::mapToResponse).toList();
    }

    // ------------------- DELETE -------------------
    @Transactional
    public EmployeeResponse deleteByEmployeeId(String employeeId) {
        EmployeeResponse employee = findByEmployeeId(employeeId);
        employeeRepository.deleteByUser_UserId(employeeId);
        return employee;
    }

    // ------------------- EXPORT -------------------
    @Override
    public List<EmployeeResponse> exportEmployees(String department, String position, BigDecimal salary) {
        Specification<Employee> spec = null;
        if (department != null) {
            spec = EmployeeSpecification.hasDepartment(department);
        }
        if (position != null) {
            spec = (spec == null) ? EmployeeSpecification.hasPosition(position)
                    : spec.and(EmployeeSpecification.hasPosition(position));
        }
        if (salary != null) {
            spec = (spec == null) ? EmployeeSpecification.hasSalary(salary)
                    : spec.and(EmployeeSpecification.hasSalary(salary));
        }
        List<Employee> employees = (spec == null) ? employeeRepository.findAll() : employeeRepository.findAll(spec);
        return employees.stream().map(this::mapToResponse).toList();
    }

    @Override
    public EmployeeResponse findByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findByUser_UserId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with employeeId: " + employeeId));
        return mapToResponse(employee);
    }


    @Override
    public EmployeeResponse findByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + email));
        return mapToResponse(employee);
    }

    @Override
    public List<EmployeeResponse> findByFirstName(String firstName) {
        List<Employee> employees = employeeRepository.findByFirstName(firstName);
        return employees.stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<EmployeeResponse> findByLastName(String lastName) {
        List<Employee> employees = employeeRepository.findByLastName(lastName);
        return employees.stream().map(this::mapToResponse).toList();
    }

    @Override
    public EmployeeResponse registerEmployee(EmployeeRequest employeeRequest) {

        String password = employeeRequest.getPassword();
        if (EmployeeGenerator.isValidPassword(password)) {
            throw new InvalidPasswordException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character!");
        }

        Employee employee = convertToEmployee(employeeRequest);
        //employee.setEmployeeId();

        // Step 2: Create system user using employeeId
        User user = new User();
        user.setUserId(generateUniqueEmployeeId());
        user.setEmail(employee.getEmail());
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
        userRepository.save(user);


        employee.setUser(user);
        Employee savedEmployee = employeeRepository.save(employee);


        // Step 3: Assign default role
        Role role = new Role();
        role.setUserRole("ROLE_EMPLOYEE");
        role.setUser(user);
        roleRepository.save(role);

        return mapToResponse(savedEmployee);
    }

    @Override
    public EmployeeResponse update(String id, EmployeeRequest employeeRequest) {
        employeeRepository.findByUser_UserId(id);
        Employee employee = convertToEmployee(employeeRequest);
        Employee savedEmployee = employeeRepository.save(employee);
        return mapToResponse(savedEmployee);
    }

    private Employee convertToEmployee(EmployeeRequest employeeRequest) {
        return employeeMapper.toEmployee(employeeRequest);
    }

    private Sort getSort(String sortBy, String sortDirection){
        Sort.Direction direction = sortDirection.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        return switch (sortBy) {
            case "lastName" -> Sort.by(direction, AppConstants.SORT_BY_LASTNAME)
                    .and(Sort.by(direction, AppConstants.SORT_BY_FIRSTNAME));
            case "department" -> Sort.by(direction, AppConstants.SORT_BY_DEPARTMENT)
                    .and(Sort.by(direction, AppConstants.SORT_BY_FIRSTNAME))
                    .and(Sort.by(direction, AppConstants.SORT_BY_LASTNAME));
            case null, default -> Sort.by(direction, AppConstants.SORT_BY_FIRSTNAME)
                    .and(Sort.by(direction, AppConstants.SORT_BY_LASTNAME));
        };
    }


    public EmployeeResponse mapToResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setEmployeeId(employee.getUser().getUserId());
        response.setFirstName(employee.getFirstName());
        response.setLastName(employee.getLastName());
        response.setEmail(employee.getEmail());
        response.setPhoneNumber(employee.getPhoneNumber());
        response.setDepartment(employee.getDepartment());
        response.setPosition(employee.getPosition());
        response.setSalary(employee.getSalary());
        response.setHireDate(employee.getHireDate());
        response.setPerformanceReview(employee.getPerformanceReview());
        response.setSkills(employee.getSkills());
        response.setActive(employee.getActive());
        return response;
    }

    /**
     * Generate a unique employee ID in EMP-XXXX format.
     */
    private String generateUniqueEmployeeId() {
        String employeeId;
        do {
            // EMP-XXXX format using first 8 characters of UUID
            employeeId = "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (employeeRepository.existsByUser_UserId(employeeId));

        return employeeId;
    }

}
