package com.pawlik.przemek.onlinegradebook.dto.grade;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class GradeStudentDto {
    @Schema(description = "Unique identifier of the grade", example = "1")
    private Long id;

    @Schema(description = "Name of the grade")
    private String name;

    @Schema(description = "Date when grade was assigned")
    private LocalDate assignedDate;

    @Schema(description = "Grade value", example = "4")
    private String grade;
}
