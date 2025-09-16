package com.amblessed.employees.service;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 16-Sep-25
 */


import com.amblessed.employees.config.EmployeeSeeder;
import com.amblessed.employees.entity.CustomUserDetails;
import com.amblessed.employees.entity.User;
import com.amblessed.employees.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user: {}", username);
        User user = userRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        if (!user.isActive()) {
            throw new UsernameNotFoundException("User is inactive: " + username);
        }
        return new CustomUserDetails(user);
    }
}
