package com.pawlik.przemek.onlinegradebook.dto.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceAddDto {

    @NotNull(message = "User id is required.")
    @Schema(description = "User id", example = "1")
    private Long userId;

    @NotNull(message = "Lesson id is required.")
    @Schema(description = "Lesson id", example = "10")
    private Long lessonId;

    @NotNull(message = "Attendance status is required.")
    @Schema(description = "Attendance status", example = "true")
    private Boolean present;
}
