package com.pawlik.przemek.onlinegradebook.dto.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AttendancesAddDto {

    @NotNull(message = "Lesson id is required.")
    @Schema(description = "Lesson id", example = "10")
    private Long lessonId;

    @NotNull(message = "Attendance list is required.")
    @NotEmpty(message = "Attendance list cannot be empty.")
    @Schema(description = "List of student attendances")
    private List<AttendanceAddDto> attendances;
}
