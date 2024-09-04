package com.pawlik.przemek.onlinegradebook.dto.grade;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GradesAddDto {

    @NotNull(message = "Subject id is required.")
    @Schema(description = "Subject id", example = "10")
    private Long subjectId;

    @NotNull(message = "Grades name is required.")
    @NotBlank(message = "Grades name cannot be blank.")
    @Schema(description = "Name of the grades", example = "Present Simple - Test")
    private String name;

    @NotNull(message = "Grades list is required.")
    @NotEmpty(message = "Grades list cannot be empty.")
    @Schema(description = "List of student grades. When not all students from class are provided in list then grade are not assigned to them.")
    private List<GradeAddDto> grades;
}
