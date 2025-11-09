package com.group7.eduscrum_awards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global Exception Handler for the entire application.
 * @ControllerAdvice: This annotation makes it a global handler.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * This method handles only DuplicateResourceException.
     * @ExceptionHandler: Catches the specific exception.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Object> handleDuplicateResourceException(
            DuplicateResourceException ex, WebRequest request) {

        // Create a clean JSON response body
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value()); // 409
        body.put("error", "Conflict");
        body.put("message", ex.getMessage()); // The message from the service class
        body.put("path", request.getDescription(false).replace("uri=", "")); // The URL

        // Return the clean response with the 409 status code
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    /**
     * This method handles only ResourceNotFoundException.
     * @ExceptionHandler: Catches the specific exception.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        // Create a clean JSON response body
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value()); // 404
        body.put("error", "Not Found");
        body.put("message", ex.getMessage()); // The message from the service class
        body.put("path", request.getDescription(false).replace("uri=", "")); // The URL

        // Return the clean response with the 404 status code
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles built-in Java IllegalArgumentException.
     * @ExceptionHandler: Catches this specific exception.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value()); // 400 Bad Request
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage()); // The message from the exception
        body.put("path", request.getDescription(false).replace("uri=", ""));

        // Return the clean response with the 400 status code
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles security-based AccessDeniedException (403).
     * This overrides the default Spring Security 403 response to return a clean JSON message.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value()); // 403
        body.put("error", "Forbidden");
        body.put("message", ex.getMessage()); // "You are not the Product Owner..."
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
}
