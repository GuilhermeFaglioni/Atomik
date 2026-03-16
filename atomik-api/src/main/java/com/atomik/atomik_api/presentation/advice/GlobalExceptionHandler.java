package com.atomik.atomik_api.presentation.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atomik.atomik_api.domain.exception.EmailAlreadyExistsException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.presentation.validation.ErrorMessageDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorMessageDTO> handleAuthException(UnauthorizedException ex) {
        var error = new ErrorMessageDTO(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessageDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        var error = new ErrorMessageDTO(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorMessageDTO> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        var error = new ErrorMessageDTO(
                HttpStatus.CONFLICT.value(),
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
