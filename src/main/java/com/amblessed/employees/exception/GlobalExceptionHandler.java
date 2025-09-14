package com.amblessed.employees.exception;

/*
 * @Project Name: employees
 * @Author: Okechukwu Bright Onwumere
 * @Created: 07-Sep-25
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;


    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class,
            SQLIntegrityConstraintViolationException.class, HandlerMethodValidationException.class, InvalidPasswordException.class})
    public ProblemDetail handleException(Exception exception) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", LocalDateTime.now().toString());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setType(URI.create("http://localhost:9090/api/v1/common-errors"));
        problemDetail.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
        problemDetail.setInstance(problemDetail.getInstance());
        problemDetail.setProperties(map);
        String detailMessage;

        switch (exception) {
            case ConstraintViolationException ex -> detailMessage = ex.getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("Validation failed");
            case MethodArgumentNotValidException ex -> detailMessage = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("Validation failed");
            case SQLIntegrityConstraintViolationException ex -> detailMessage = ex.getMessage();
            case HandlerMethodValidationException ex ->
                detailMessage = "id " + Optional.of(ex.getDetailMessageArguments())
                        .filter(args -> args.length > 0 && args[0] != null)
                        .map(args -> args[0].toString())
                        .orElse("Validation failed");
            default -> detailMessage = exception.getMessage();
            }

        problemDetail.setDetail(detailMessage);
        return problemDetail;
        }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleBookNotFoundException(ResourceNotFoundException exception) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", LocalDateTime.now().toString());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setType(URI.create("http://localhost:8080/api/v1/common-errors"));
        problemDetail.setTitle(HttpStatus.NOT_FOUND.getReasonPhrase());
        problemDetail.setDetail(exception.getMessage());
        problemDetail.setProperties(map);
        return problemDetail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleAccessDenied(DataIntegrityViolationException ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", LocalDateTime.now().toString());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setType(URI.create("http://localhost:8080/api/v1/common-errors"));
        if (ex.getCause() instanceof ConstraintViolationException || ex.getMessage().contains("employee_email_key")) {
            problemDetail.setTitle(HttpStatus.CONFLICT.getReasonPhrase());
            problemDetail.setDetail("Email already exists.");
            problemDetail.setProperties(map);
        }
        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException exception) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", LocalDateTime.now().toString());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setType(URI.create("http://localhost:8080/api/v1/common-errors"));
        problemDetail.setTitle(HttpStatus.FORBIDDEN.getReasonPhrase());
        problemDetail.setDetail("You are not authorized to access this resource.");
        problemDetail.setProperties(map);
        return problemDetail;
    }


    public void writeProblemDetail(HttpServletResponse response, ProblemDetail detail) throws IOException {
        response.setContentType("application/json");
        response.setStatus(detail.getStatus());
        response.getWriter().write(objectMapper.writeValueAsString(detail));
    }

    public ProblemDetail createProblemDetail(Exception exception, HttpStatus status) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", LocalDateTime.now().toString());
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(URI.create("http://localhost:8080/api/v1/common-errors"));
        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setDetail(exception.getMessage());
        problemDetail.setProperties(map);
        return problemDetail;
    }


}

