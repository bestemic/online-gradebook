package com.pawlik.przemek.onlinegradebook.dto.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddLessonAttendancesDto(
        @NotNull(message = "Lesson ID is required.")
        @Schema(description = "The unique identifier of the lesson for which attendance is being recorded", example = "10")
        Long lessonId,

        @NotNull(message = "Attendance list is required.")
        @NotEmpty(message = "Attendance list cannot be empty.")
        @Schema(description = "A list of attendance records for students in the specified lesson")
        List<AddAttendanceDto> attendances
) {
}
