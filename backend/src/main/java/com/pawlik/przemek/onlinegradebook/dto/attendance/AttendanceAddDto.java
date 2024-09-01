package com.pawlik.przemek.onlinegradebook.dto.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceAddDto {

    @NotNull(message = "Student id is required.")
    @Schema(description = "Student id", example = "1")
    private Long studentId;

    @NotNull(message = "Attendance status is required.")
    @Schema(description = "Attendance status", example = "true")
    private Boolean present;
}
