package com.amblessed.employees.service;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 14-Sep-25
 */


import com.amblessed.employees.entity.*;
import com.amblessed.employees.repository.EmployeeRepository;
import com.amblessed.employees.repository.RoleRepository;
import com.amblessed.employees.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(UserDTO dto) {


        if (userRepository.existsByUserId(dto.getUserId())) {
            throw new RuntimeException("User ID already exists!");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        // Save user
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setActive(true);

        userRepository.save(user);

        /*// Assign default role
        Role role = new Role();
        role.setUserId(user.getUserId());
        role.setRole("ROLE_EMPLOYEE");
        roleRepository.save(role);*/
    }
}
