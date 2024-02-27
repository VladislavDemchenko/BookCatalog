package org.example.bookcatalog.controller.controllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import org.example.bookcatalog.exception.InvalidRequestException;
import org.example.bookcatalog.exception.UnsupportedEntityFieldException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(EntityNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnsupportedEntityFieldException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(UnsupportedEntityFieldException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.HTTP_VERSION_NOT_SUPPORTED.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}

