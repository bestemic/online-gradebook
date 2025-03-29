package com.pawlik.przemek.onlinegradebook.dto.attendance;

import com.pawlik.przemek.onlinegradebook.dto.user.UserBasicDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record GetAttendanceDto(
        @Schema(description = "The unique identifier for this attendance record", example = "1")
        Long id,

        @Schema(description = "The student for whom attendance is being recorded")
        UserBasicDto student,

        @Schema(description = "The student's attendance status (true if present, false if absent)", example = "true")
        Boolean present
) {
}
