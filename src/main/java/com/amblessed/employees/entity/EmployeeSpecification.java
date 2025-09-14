package com.amblessed.employees.entity;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 13-Sep-25
 */


import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class EmployeeSpecification {

    public static Specification<Employee> hasDepartment(String department) {
        return (root, query, cb) ->
                department == null ? null : cb.equal(root.get("department"), department);
    }

    public static Specification<Employee> hasPosition(String position) {
        return (root, query, cb) ->
                position == null ? null : cb.equal(root.get("position"), position);
    }

    public static Specification<Employee> hasSalary(BigDecimal salary) {
        return (root, query, cb) ->
                salary == null ? null : cb.greaterThanOrEqualTo(root.get("salary"), salary);
    }

}
