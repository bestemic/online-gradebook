package com.pawlik.przemek.onlinegradebook.dto.grade;

import com.pawlik.przemek.onlinegradebook.dto.subject.SubjectDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GradesDto {

    @Schema(description = "Subject on which grade is being marked")
    private SubjectDto subject;

    @Schema(description = "Name of the grade")
    private String name;

    @Schema(description = "Date when grade was assigned")
    private LocalDate assignedDate;

    @Schema(description = "List of students grades")
    private List<GradeDto> grades;
}
