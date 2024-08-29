package com.pawlik.przemek.onlinegradebook.dto.school_class;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SchoolClassBasicDto {

    @Schema(description = "Unique identifier of the class", example = "1")
    private Long id;

    @Schema(description = "Name of the class", example = "1B")
    private String name;

    @Schema(description = "Room assigned to the class", example = "A1")
    private String classroom;
}