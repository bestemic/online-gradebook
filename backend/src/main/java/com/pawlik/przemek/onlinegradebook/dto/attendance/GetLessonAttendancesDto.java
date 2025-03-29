package com.pawlik.przemek.onlinegradebook.dto.attendance;

import com.pawlik.przemek.onlinegradebook.dto.lesson.GetLessonDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record GetLessonAttendancesDto(
        @Schema(description = "The lesson for which attendance is being recorded")
        GetLessonDto lesson,

        @Schema(description = "A list of student attendance records for the lesson")
        List<GetAttendanceDto> attendances
) {
}
