package com.pawlik.przemek.onlinegradebook.dto.attendance;

import com.pawlik.przemek.onlinegradebook.dto.lesson.LessonDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AttendancesLessonDto {

    @Schema(description = "Lesson on which attendance is being marked")
    private LessonDto lesson;

    @Schema(description = "List of students attendance")
    private List<AttendanceDto> attendances;
}
