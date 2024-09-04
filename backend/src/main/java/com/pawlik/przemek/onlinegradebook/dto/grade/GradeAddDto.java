package com.pawlik.przemek.onlinegradebook.dto.grade;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeAddDto {

    @NotNull(message = "Student id is required.")
    @Schema(description = "Student id", example = "1")
    private Long studentId;

    @Pattern(regexp = "[123456]", message = "Grade must be one of the following values: 1, 2, 3, 4, 5, 6")
    @Schema(description = "Grade value (if not provided, the grade will not be assigned to the student)", example = "4", nullable = true)
    private String grade;
}
