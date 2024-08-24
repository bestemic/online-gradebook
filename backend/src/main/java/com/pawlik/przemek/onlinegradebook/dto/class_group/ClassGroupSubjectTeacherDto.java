package com.pawlik.przemek.onlinegradebook.dto.class_group;

import com.pawlik.przemek.onlinegradebook.dto.subject.SubjectDto;
import com.pawlik.przemek.onlinegradebook.dto.user.UserBasicDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ClassGroupSubjectTeacherDto {

    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    @Schema(description = "Class")
    private ClassGroupBasicDto classGroup;

    @Schema(description = "Student assigned to the class")
    private SubjectDto subject;

    @Schema(description = "Teacher assigned to the class")
    private UserBasicDto teacher;
}
