package com.askisi5.app.rest.exception;

/**
 * Thrown when an attempt is made to create a resource that already exists
 * (e.g. a Book whose ISBN is already in use). Mapped to HTTP 409 by
 * {@link GlobalExceptionHandler}.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
