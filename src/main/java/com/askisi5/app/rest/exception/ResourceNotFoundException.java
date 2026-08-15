package com.askisi5.app.rest.exception;

/**
 * Thrown when a requested resource (e.g. a Book by ISBN or a User by id)
 * does not exist. Mapped to HTTP 404 by {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
