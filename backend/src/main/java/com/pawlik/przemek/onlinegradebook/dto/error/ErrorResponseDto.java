package com.pawlik.przemek.onlinegradebook.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ErrorResponseDto {

    @Schema(description = "HTTP status code of the error", example = "401")
    private int status;

    @Schema(description = "Title describing the type of error", example = "Unauthorized")
    private String title;

    @Schema(description = "Details error message", example = "Invalid email")
    private String message;
}
