package com.amblessed.employees.entity;



/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 09-Sep-25
 */



import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeRequest {


    @Size(min = 2, max = 20, message = "First name must be between 2 and 20 characters")
    private String firstName;

    @Size(min = 2, max = 20, message = "Last name must be between 2 and 20 characters")
    private String lastName;

    @Email(message = "Email should be valid")
    @Size(max = 50, message = "Email must not exceed 50 characters")
    private String email;

    @Size(min = 10, max = 10, message = "Phone number must be between 10 characters")
    private String phoneNumber;

    @Size(min = 5, message = "Department must be at least 5 Characters long")
    private String department;

    @Size(min = 5, message = "Position must be at least 5 Characters long")
    private String position;

    @Min(value = 40000, message = "Salary must be at least 50000")
    private BigDecimal salary;

    @PastOrPresent(message = "Hire date must be in the past or present")
    private LocalDate hireDate;

    private String performanceReview;

    private String skills;
    private Boolean active;

    /*@JsonCreator
    public EmployeeRequest(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("email") String email) {
        this.firstName = firstName != null ? firstName.trim() : null;
        this.lastName = lastName != null ? lastName.trim() : null;
        this.email = email != null ? email.trim() : null;
    }*/
}
