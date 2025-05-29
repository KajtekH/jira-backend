package com.kajtekh.jirabackend.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<Object> handleAccessDeniedException(final AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(APPLICATION_JSON)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(TabException.class)
    @ResponseBody
    public ResponseEntity<Object> handleTabException(final TabException exception) {
        return ResponseEntity.status(BAD_REQUEST)
                .contentType(APPLICATION_JSON)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<Object> handleMethodArgumentNotValidException(final ConstraintViolationException exception) {
        return ResponseEntity.status(BAD_REQUEST)
                .contentType(APPLICATION_JSON)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Object> handleException(final Exception exception) {
        final var logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
        logger.error("An unexpected error occurred: {}", exception.getMessage(), exception);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .contentType(APPLICATION_JSON)
                .body(new ErrorResponse(exception.getMessage()));
    }

}
