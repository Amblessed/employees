package com.amblessed.employees.entity;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 14-Sep-25
 */

import lombok.*;

@Data
public class UserDTO {

    private String userId;
    private String password;
    private boolean active;
    private String email;
}
