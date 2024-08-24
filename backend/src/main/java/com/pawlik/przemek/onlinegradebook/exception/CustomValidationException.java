package com.pawlik.przemek.onlinegradebook.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomValidationException extends RuntimeException {
    private String field;

    public CustomValidationException(String field, String message) {
        super(message);
        this.field = field;
    }
}
