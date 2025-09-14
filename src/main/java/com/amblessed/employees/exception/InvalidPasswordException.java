package com.amblessed.employees.exception;



/*
 * @Project Name: books
 * @Author: Okechukwu Bright Onwumere
 * @Created: 07-Sep-25
 */


public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(String message) {
        super(message);
    }
}
