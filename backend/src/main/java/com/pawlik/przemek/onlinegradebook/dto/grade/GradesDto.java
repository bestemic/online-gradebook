package com.pawlik.przemek.onlinegradebook.dto.grade;

import com.pawlik.przemek.onlinegradebook.dto.subject.SubjectDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GradesDto {

    @Schema(description = "Subject on which grade is being marked")
    private SubjectDto subject;

    @Schema(description = "Name of the grade")
    private String name;

    @Schema(description = "Time when grade was assigned")
    private LocalDateTime assignedTime;

    @Schema(description = "List of students grades")
    private List<GradeDto> grades;
}
