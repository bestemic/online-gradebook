package com.pawlik.przemek.onlinegradebook.dto.attendance;

import com.pawlik.przemek.onlinegradebook.dto.user.UserBasicDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AttendanceDto {

    @Schema(description = "Student whose attendance is being marked")
    private UserBasicDto student;

    @Schema(description = "Attendance status", example = "true")
    private Boolean present;
}
