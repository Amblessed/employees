package com.amblessed.employees.mapper;

import com.amblessed.employees.entity.Employee;
import com.amblessed.employees.entity.EmployeeRequest;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Employee and EmployeeRequest objects.
 */
@Component
public class EmployeeMapper {

    /**
     * Converts an EmployeeRequest to an Employee entity.
     *
     * @param employeeRequest the request to convert
     * @return the converted Employee, or null if input is null
     */
    public Employee toEmployee(EmployeeRequest employeeRequest) {
        return employeeRequest == null ? null : mapFields(employeeRequest, new Employee());
    }

    /**
     * Converts an Employee to an EmployeeRequest.
     *
     * @param employee the employee to convert
     * @return the converted EmployeeRequest, or null if input is null
     */
    public EmployeeRequest toEmployeeRequest(Employee employee) {
        return employee == null ? null : mapFields(employee, new EmployeeRequest());
    }

    /**
     * Generic method to map common fields between Employee and EmployeeRequest.
     *
     * @param source the source object
     * @param target the target object
     * @return the populated target
     */
    private <S, T> T mapFields(S source, T target) {
        if (source instanceof EmployeeRequest req && target instanceof Employee emp) {
            emp.setFirstName(req.getFirstName());
            emp.setLastName(req.getLastName());
            emp.setEmail(req.getEmail());
            emp.setPhoneNumber(req.getPhoneNumber());
            emp.setSalary(req.getSalary());
            emp.setPosition(req.getPosition());
            emp.setDepartment(req.getDepartment());
            emp.setHireDate(req.getHireDate());
            emp.setPerformanceReview(req.getPerformanceReview());
            emp.setSkills(req.getSkills());
            emp.setActive(req.getActive());
        } else if (source instanceof Employee emp && target instanceof EmployeeRequest req) {
            req.setFirstName(emp.getFirstName());
            req.setLastName(emp.getLastName());
            req.setEmail(emp.getEmail());
            req.setPhoneNumber(emp.getPhoneNumber());
            req.setDepartment(emp.getDepartment());
            req.setPosition(emp.getPosition());
            req.setHireDate(emp.getHireDate());
            req.setPerformanceReview(emp.getPerformanceReview());
            req.setSkills(emp.getSkills());
            req.setActive(emp.getActive());
        }
        return target;
    }
}
