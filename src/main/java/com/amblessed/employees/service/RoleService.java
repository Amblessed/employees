package com.amblessed.employees.service;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 14-Sep-25
 */

import com.amblessed.employees.entity.Role;
import com.amblessed.employees.entity.User;
import com.amblessed.employees.exception.AlreadyExistsException;
import com.amblessed.employees.exception.ResourceNotFoundException;
import com.amblessed.employees.repository.RoleRepository;
import com.amblessed.employees.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;


    @Transactional
    public void assignRole(String userId, String roleName) {
        // Check if user exists
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user already has this role
        boolean alreadyAssigned = roleRepository.existsByUserUserIdAndUserRole(userId, roleName);
        if (alreadyAssigned) {
            throw new AlreadyExistsException("User already has role: " + roleName);
        }

        // Save new role
        Role role = new Role();
        role.setUser(user);
        role.setUserRole(roleName);
        roleRepository.save(role);
    }
}
