package com.pawlik.przemek.onlinegradebook.dto.subject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectAddDto {

    @NotNull(message = "Subject name is required.")
    @NotBlank(message = "Subject name must not be blank.")
    @Size(min = 2, max = 50, message = "Subject name length must be between 2 and 50 characters.")
    @Schema(description = "Subject name", example = "English")
    private String name;
}
