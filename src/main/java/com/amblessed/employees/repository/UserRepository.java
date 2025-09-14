package com.amblessed.employees.repository;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 14-Sep-25
 */


import com.amblessed.employees.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String userId);
    boolean existsByEmail(String email);
    boolean existsByUserId(String userId);
}
