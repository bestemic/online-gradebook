package com.pawlik.przemek.onlinegradebook.dto.attendance;

import com.pawlik.przemek.onlinegradebook.dto.lesson.LessonDto;
import com.pawlik.przemek.onlinegradebook.dto.user.UserBasicDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AttendanceDto {

    @Schema(description = "Unique identifier of the attendance", example = "1")
    private Long id;

    @Schema(description = "Student whose attendance is being marked")
    private UserBasicDto student;

    @Schema(description = "Lesson on which attendance is being marked")
    private LessonDto lessonDto;

    @Schema(description = "Attendance status", example = "true")
    private Boolean present;
}
