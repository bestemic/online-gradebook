package com.pawlik.przemek.onlinegradebook.dto.subject;

import com.pawlik.przemek.onlinegradebook.dto.user.UserBasicDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
public class SubjectDto {

    @Schema(description = "Unique identifier of the subject", example = "1")
    private Long id;

    @Schema(description = "Name of the subject", example = "English")
    private String name;

    @Schema(description = "List of teachers that teach the subject")
    private Set<UserBasicDto> teachers;
}

