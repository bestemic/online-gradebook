package com.pawlik.przemek.onlinegradebook.dto.lesson;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class LessonDto {

    @Schema(description = "Lesson title", example = "Present Simple Tense")
    private String title;

    @Schema(description = "Lesson description", example = "Present Simple Tense - theory and exercises.")
    private String description;

    @Schema(description = "Date of lesson", example = "2024-01-01")
    private LocalDate date;

    @Schema(description = "Class-Subject-Teacher id", example = "5")
    private Long classGroupSubjectTeacherId;

}
