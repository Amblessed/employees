package com.amblessed.employees.entity;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 13-Sep-25
 */


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "system_users")
@Builder
public class User {

    @Id
    @Column(name = "user_id", nullable = false, length = 20, unique = true)
    private String userId;

    @Column(nullable = false, length = 68)
    private String password;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, unique = true, length = 75)
    private String email;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Employee employee;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Role> roles = new HashSet<>();

}
