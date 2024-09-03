package com.pawlik.przemek.onlinegradebook.dto.material;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MaterialDto {

    @Schema(description = "Material id", example = "1")
    private Long id;

    @Schema(description = "Material name", example = "Present Simple Tense")
    private String name;

    @Schema(description = "Material description", example = "Present Simple Tense - theory and exercises.")
    private String description;

    @Schema(description = "Time of material publication", example = "2024-01-01T10:15:30")
    private LocalDateTime publicationTime;
}