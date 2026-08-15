package com.askisi5.app.rest.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Direct unit tests for the two "last resort" handlers in
 * {@link GlobalExceptionHandler}: {@link DataIntegrityViolationException}
 * (e.g. a DB constraint violated outside of our own duplicate/not-found
 * checks, such as a race between two concurrent requests) and the generic
 * {@link Exception} catch-all. Both are deliberately hard to provoke through
 * a full MockMvc round-trip, since they only fire for failure modes our own
 * validation and service-layer checks don't already catch - so they're
 * exercised directly here instead, the same way any other POJO method would
 * be tested.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("DataIntegrityViolationException maps to 409 with a generic, non-leaking message")
    void handlesDataIntegrityViolation() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Duplicate entry 'x' for key 'PRIMARY'");

        ResponseEntity<Map<String, String>> response = handler.handleDataIntegrityViolation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error"))
                .isEqualTo("The request could not be completed because it conflicts with existing data")
                // the raw DB/driver message must never leak to the client
                .doesNotContain("PRIMARY");
    }

    @Test
    @DisplayName("Unexpected exceptions map to 500 with a generic, non-leaking message")
    void handlesGeneralExceptions() {
        RuntimeException ex = new RuntimeException("some internal detail that should not reach the client");

        ResponseEntity<Map<String, String>> response = handler.handleGeneralExceptions(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Internal server error");
    }
}
