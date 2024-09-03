package com.pawlik.przemek.onlinegradebook.dto.material;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialAddDto {

    @NotNull(message = "Material name is required.")
    @NotBlank(message = "Material name must not be blank.")
    @Size(min = 2, max = 50, message = "Material name length must be between 2 and 50 characters.")
    @Schema(description = "Material name", example = "Exercise 1")
    private String name;

    @NotNull(message = "Material description is required.")
    @NotBlank(message = "Material description must not be blank.")
    @Size(min = 2, message = "Material description length must be at least 2 characters.")
    @Schema(description = "Material description", example = "Present Simple Tense additional exercises.")
    private String description;

    @NotNull(message = "Subject id is required.")
    @Schema(description = "Subject id", example = "5")
    private Long subjectId;
}
