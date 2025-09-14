package com.amblessed.employees.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(
                "ROLE_ADMIN > ROLE_MANAGER\nROLE_MANAGER > ROLE_EMPLOYEE"
        );
    }

    @Bean
    public MethodSecurityExpressionHandler expressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    @Bean
    public UserDetailsManager userDetailsManager(DataSource datasource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(datasource);
        manager.setUsersByUsernameQuery(
                "SELECT user_id, password, active FROM system_users WHERE user_id = ?"
        );
        // Load authorities by joining roles table
        manager.setAuthoritiesByUsernameQuery(
                "SELECT su.user_id, r.user_role " +
                        "FROM roles r " +
                        "JOIN system_users su ON su.user_id = r.user_id " +
                        "WHERE su.user_id = ?"
        );

        //manager.setRolePrefix("");

        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CustomAuthEntryPoint customAuthEntryPoint,
                                                   CustomAccessDeniedHandler customAccessDeniedHandler) throws Exception {

        http
                .authorizeHttpRequests(configurer ->
                        configurer
                                .requestMatchers("/docs/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs").permitAll()
                                .requestMatchers(HttpMethod.GET, "/h2-console/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/h2-console/**").permitAll()
                                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                                .requestMatchers("/api/**").authenticated()

                                // Registration endpoint (open for new users)
                                //.requestMatchers(HttpMethod.POST, "/api/auth/register").hasRole("ADMIN")

                                // Admin-only endpoints
                                /*.requestMatchers(HttpMethod.POST,"/api/admin/assign-role", "/api/auth/register").hasRole("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/api/employees/id/**").authenticated() // checked by PreAuthorize
                                .requestMatchers(HttpMethod.GET, "/api/employees/**", "/api/employees").hasAnyRole("MANAGER")
                                .requestMatchers(HttpMethod.POST,  "/api/employees/").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/employees/**").hasRole("MANAGER")
                                .requestMatchers(HttpMethod.DELETE, "/api/employees/id/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/employees/download").hasAnyRole("MANAGER", "ADMIN")*/
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .exceptionHandling(configurer ->
                        configurer
                                .authenticationEntryPoint(customAuthEntryPoint) // for 401
                                .accessDeniedHandler(customAccessDeniedHandler)   // for 403
                );
        return http.build();
    }
}
