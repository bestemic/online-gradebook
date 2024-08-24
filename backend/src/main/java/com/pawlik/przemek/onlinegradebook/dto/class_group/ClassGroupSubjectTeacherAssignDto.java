package com.pawlik.przemek.onlinegradebook.dto.class_group;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassGroupSubjectTeacherAssignDto {

    @NotNull(message = "Subject id is required.")
    @Schema(description = "Subject id", example = "2")
    private Long subjectId;

    @NotNull(message = "Teacher id is required.")
    @Schema(description = "Teacher id", example = "5")
    private Long teacherId;
}
