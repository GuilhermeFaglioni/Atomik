package com.atomik.atomik_api.presentation.advice;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atomik.atomik_api.domain.exception.AccountNotFoundException;
import com.atomik.atomik_api.domain.exception.AuditLogNotFoundException;
import com.atomik.atomik_api.domain.exception.BudgetNotFoundException;
import com.atomik.atomik_api.domain.exception.CategoryNotFoundException;
import com.atomik.atomik_api.domain.exception.EmailAlreadyExistsException;
import com.atomik.atomik_api.domain.exception.RecurringTransactionNotFoundException;
import com.atomik.atomik_api.domain.exception.TransactionNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.presentation.validation.ErrorMessageDTO;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            UserNotFoundException.class,
            AccountNotFoundException.class,
            CategoryNotFoundException.class,
            BudgetNotFoundException.class,
            TransactionNotFoundException.class,
            AuditLogNotFoundException.class,
            RecurringTransactionNotFoundException.class
    })
    public ResponseEntity<ErrorMessageDTO> handleNotFoundExceptions(RuntimeException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorMessageDTO> handleAuthException(UnauthorizedException ex) {
        return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessageDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorMessageDTO> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return buildError(HttpStatus.BAD_REQUEST, message.isBlank() ? "Invalid request body" : message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessageDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Invalid request body");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessageDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<ErrorMessageDTO> buildError(HttpStatus status, String message) {
        var error = new ErrorMessageDTO(status.value(), message);
        return ResponseEntity.status(status).body(error);
    }
}
