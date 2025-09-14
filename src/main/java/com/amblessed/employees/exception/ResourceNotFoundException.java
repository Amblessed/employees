package com.amblessed.employees.exception;



/*
 * @Project Name: books
 * @Author: Okechukwu Bright Onwumere
 * @Created: 07-Sep-25
 */


public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
