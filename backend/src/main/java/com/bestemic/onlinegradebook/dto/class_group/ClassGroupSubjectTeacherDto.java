package com.bestemic.onlinegradebook.dto.class_group;

import com.bestemic.onlinegradebook.dto.subject.SubjectDto;
import com.bestemic.onlinegradebook.dto.user.UserBasicDto;
import com.bestemic.onlinegradebook.model.ClassGroup;
import com.bestemic.onlinegradebook.model.Subject;
import com.bestemic.onlinegradebook.model.User;
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
    private ClassGroupDto classGroup;

    @Schema(description = "Student assigned to the class")
    private SubjectDto subject;

    @Schema(description = "Teacher assigned to the class")
    private UserBasicDto teacher;
}
