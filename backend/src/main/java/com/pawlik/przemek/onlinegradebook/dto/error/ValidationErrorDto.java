package com.pawlik.przemek.onlinegradebook.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class ValidationErrorDto {

    @Schema(description = "Name of the field where validation error occurred", example = "email")
    private String field;

    @Schema(description = "List of error messages associated with field", example = "[\"Invalid format\", \"Field cannot be empty\"]")
    private List<String> errors;
}
