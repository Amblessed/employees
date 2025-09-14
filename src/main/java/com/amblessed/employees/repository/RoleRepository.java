package com.amblessed.employees.repository;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 14-Sep-25
 */


import com.amblessed.employees.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByUserUserId(String userId);
    boolean existsByUserUserIdAndUserRole(String userId, String role);
}
