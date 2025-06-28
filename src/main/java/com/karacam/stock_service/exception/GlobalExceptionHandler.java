package com.karacam.stock_service.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ProblemDetail> handleValidationErrors(ValidationException validationException) {
        String message = validationException.getMessage();
        return ResponseEntity.badRequest().body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatusExceptions(ResponseStatusException responseStatusException) {
        HttpStatus status = HttpStatus.valueOf(responseStatusException.getStatusCode().value());
        String message = responseStatusException.getReason() != null ? responseStatusException.getReason() : status.getReasonPhrase();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);

        return ResponseEntity.status(status).body(problemDetail);
    }
}
