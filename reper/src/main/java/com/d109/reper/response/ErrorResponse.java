package com.d109.reper.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ErrorResponse(String message) {

    public static ResponseEntity<ErrorResponse> create(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(message));
    }

    // 400 IllegalArgumentException
    public static ResponseEntity<ErrorResponse> badRequest(String message) {
        return create(HttpStatus.BAD_REQUEST, message);
    }

    // 404
    // EntityNotFoundException
    // NoSuchElementException
    public static ResponseEntity<ErrorResponse> notFound(String message) {
        return create(HttpStatus.NOT_FOUND, message);
    }

    // 409 IllegalStateException
    public static ResponseEntity<ErrorResponse> conflict(String message) {
        return create(HttpStatus.CONFLICT, message);
    }

    // 500 Exception
    public static ResponseEntity<ErrorResponse> internalServerError(String messgae) {
        return create(HttpStatus.INTERNAL_SERVER_ERROR, messgae);
    }
}
