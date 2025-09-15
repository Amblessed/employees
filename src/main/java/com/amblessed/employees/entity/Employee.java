package com.amblessed.employees.entity;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 08-Sep-25
 */

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "employees")
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Read-only mirror of the foreign key column
    @Column(name = "employee_id", nullable = false, unique = true, insertable = false, updatable = false)
    private String employeeId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 75)
    private String email;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "department", nullable = false, length = 50)
    private String department;

    @Column(name = "position", nullable = false, length = 50)
    private String position;

    @Column(name = "salary", nullable = false)
    private BigDecimal salary;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "performance_review", columnDefinition = "TEXT")
    private String performanceReview;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    //@CreationTimestamp
    //@Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //@UpdateTimestamp
    //@Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "active")
    private Boolean active = true;

    // Link to User (login credentials)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id", referencedColumnName = "user_id", nullable = false, unique = true)
    private User user;



    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
