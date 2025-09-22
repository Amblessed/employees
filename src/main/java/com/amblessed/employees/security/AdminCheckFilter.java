package com.amblessed.employees.security;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 22-Sep-25
 */


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AdminCheckFilter extends OncePerRequestFilter {

    private final EmployeeSecurity employeeSecurity;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Apply only to create employee endpoint
        if (request.getMethod().equals("POST") && request.getRequestURI().equals("/api/employees")) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (!employeeSecurity.isAdmin(authentication)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can create employees");
                return; // Stop filter chain; request never reaches controller
            }
        }

        filterChain.doFilter(request, response);
    }
}
