package com.pawlik.przemek.onlinegradebook.dto.grade;

import com.pawlik.przemek.onlinegradebook.dto.user.UserBasicDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GradeDto {
    @Schema(description = "Unique identifier of the grade", example = "1")
    private Long id;

    @Schema(description = "Student whose grade is being recorded")
    private UserBasicDto student;

    @Schema(description = "Grade value", example = "4")
    private String grade;
}
