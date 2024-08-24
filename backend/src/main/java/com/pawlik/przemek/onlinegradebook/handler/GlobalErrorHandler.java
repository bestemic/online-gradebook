package com.pawlik.przemek.onlinegradebook.handler;

import com.pawlik.przemek.onlinegradebook.dto.error.ErrorResponseDto;
import com.pawlik.przemek.onlinegradebook.dto.error.ValidationErrorDto;
import com.pawlik.przemek.onlinegradebook.exception.CustomValidationException;
import com.pawlik.przemek.onlinegradebook.exception.InvalidEmailException;
import com.pawlik.przemek.onlinegradebook.exception.InvalidPasswordException;
import com.pawlik.przemek.onlinegradebook.exception.NotFoundException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<List<ValidationErrorDto>> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<ValidationErrorDto> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ))
                .entrySet()
                .stream()
                .map(entry -> new ValidationErrorDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return ResponseEntity.status(BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(CustomValidationException.class)
    @ResponseBody
    public ResponseEntity<List<ValidationErrorDto>> handleCustomValidationExceptions(CustomValidationException e) {
        return ResponseEntity.status(BAD_REQUEST).body(List.of(new ValidationErrorDto(e.getField(), List.of(e.getMessage()))));
    }

    @ExceptionHandler(InvalidEmailException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleBadEmail(InvalidEmailException e) {
        return ResponseEntity.status(UNAUTHORIZED).body(new ErrorResponseDto(UNAUTHORIZED.value(), UNAUTHORIZED.getReasonPhrase(), e.getMessage()));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleBadPassword(InvalidPasswordException e) {
        return ResponseEntity.status(UNAUTHORIZED).body(new ErrorResponseDto(UNAUTHORIZED.value(), UNAUTHORIZED.getReasonPhrase(), e.getMessage()));
    }

    @ExceptionHandler(JwtException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleBadJwt() {
        return ResponseEntity.status(UNAUTHORIZED).body(new ErrorResponseDto(UNAUTHORIZED.value(), UNAUTHORIZED.getReasonPhrase(), "Invalid JWT"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(FORBIDDEN).body(new ErrorResponseDto(FORBIDDEN.value(), FORBIDDEN.getReasonPhrase(), e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleNoAuthentication(AuthenticationException e) {
        return ResponseEntity.status(UNAUTHORIZED).body(new ErrorResponseDto(UNAUTHORIZED.value(), UNAUTHORIZED.getReasonPhrase(), e.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleNoResourceFound(NoResourceFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(new ErrorResponseDto(NOT_FOUND.value(), NOT_FOUND.getReasonPhrase(), e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(new ErrorResponseDto(NOT_FOUND.value(), NOT_FOUND.getReasonPhrase(), e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(CONFLICT).body(new ErrorResponseDto(CONFLICT.value(), CONFLICT.getReasonPhrase(), e.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleUnsupportedMethod(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(METHOD_NOT_ALLOWED).body(new ErrorResponseDto(METHOD_NOT_ALLOWED.value(), METHOD_NOT_ALLOWED.getReasonPhrase(), e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleMessageNotReadable(HttpMessageNotReadableException e) {
        if (e.getMessage().startsWith("Required request body is missing")) {
            return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponseDto(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), "Missing request body"));
        } else {
            return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponseDto(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), "Invalid request body"));
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> handleOtherErrors(Exception e) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(INTERNAL_SERVER_ERROR.value(), INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage()));
    }
}
