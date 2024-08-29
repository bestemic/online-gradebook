package com.pawlik.przemek.onlinegradebook.dto.subject;

import com.pawlik.przemek.onlinegradebook.dto.school_class.SchoolClassBasicDto;
import com.pawlik.przemek.onlinegradebook.dto.user.UserBasicDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SubjectDto {

    @Schema(description = "Unique identifier of the subject", example = "1")
    private Long id;

    @Schema(description = "Name of the subject", example = "English")
    private String name;

    @Schema(description = "Class that the subject is assigned to")
    private SchoolClassBasicDto schoolClass;

    @Schema(description = "Teacher assigned to the subject")
    private UserBasicDto teacher;
}

