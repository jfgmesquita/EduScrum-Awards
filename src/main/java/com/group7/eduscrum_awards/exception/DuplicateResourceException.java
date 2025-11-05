package com.group7.eduscrum_awards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 
 * Custom exception for when a resource already exists (e.g., duplicate name/email) 
 * @ResponseStatus(HttpStatus.CONFLICT) tells Spring to return a 409 Conflict
 * status code if this exception is not caught by a @ControllerAdvice.
*/
@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
