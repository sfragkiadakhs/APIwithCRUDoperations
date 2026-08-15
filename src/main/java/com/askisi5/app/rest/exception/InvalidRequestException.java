package com.askisi5.app.rest.exception;

/**
 * Thrown when a request is well-formed and passes bean validation but is
 * semantically inconsistent (e.g. the ISBN in a PUT body does not match the
 * ISBN in the URL path). Mapped to HTTP 400 by {@link GlobalExceptionHandler}.
 */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
