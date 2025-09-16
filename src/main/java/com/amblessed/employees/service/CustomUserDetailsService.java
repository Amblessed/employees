package com.amblessed.employees.service;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 16-Sep-25
 */


import com.amblessed.employees.entity.CustomUserDetails;
import com.amblessed.employees.entity.User;
import com.amblessed.employees.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Loading user: " + username);
        User user = userRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        if (!user.isActive()) {
            throw new UsernameNotFoundException("User is inactive: " + username);
        }
        return new CustomUserDetails(user);
    }
}
