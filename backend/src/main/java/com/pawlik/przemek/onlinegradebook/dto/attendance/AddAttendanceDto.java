package com.pawlik.przemek.onlinegradebook.dto.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AddAttendanceDto(
        @NotNull(message = "Student ID is required and must be a valid identifier.")
        @Schema(description = "The unique identifier of the student", example = "1")
        Long studentId,

        @NotNull(message = "Attendance status is required and must be either true (present) or false (absent).")
        @Schema(description = "The student's attendance status (true if present, false if absent)", example = "true")
        Boolean present
) {
}
