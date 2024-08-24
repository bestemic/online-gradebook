package com.pawlik.przemek.onlinegradebook.dto.class_group;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClassGroupAddDto {

    @NotNull(message = "Name is required.")
    @NotBlank(message = "Name must not be blank.")
    @Size(min = 1, max = 50, message = "Name length must be between 1 and 50 characters.")
    @Schema(description = "Class name", example = "1B")
    private String name;

    @NotNull(message = "Classroom is required.")
    @NotBlank(message = "Classroom must not be blank.")
    @Size(min = 1, max = 50, message = "Classroom length must be between 1 and 50 characters.")
    @Schema(description = "Classroom", example = "16C")
    private String classroom;

    @NotEmpty(message = "At least one student is required.")
    @Schema(description = "Student IDs", example = "[1, 2]")
    private List<Long> studentsIds;
}
