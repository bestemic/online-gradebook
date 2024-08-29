package com.pawlik.przemek.onlinegradebook.dto.lesson;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class LessonAddDto {

    @NotNull(message = "Lesson title is required.")
    @NotBlank(message = "Lesson title must not be blank.")
    @Size(min = 2, max = 50, message = "Lesson name length must be between 2 and 50 characters.")
    @Schema(description = "Lesson title", example = "Present Simple Tense")
    private String title;

    @NotNull(message = "Lesson description is required.")
    @NotBlank(message = "Lesson description must not be blank.")
    @Size(min = 2, message = "Lesson description length must be at least 2 characters.")
    @Schema(description = "Lesson description", example = "Present Simple Tense - theory and exercises.")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Date of lesson", example = "2024-01-01")
    private LocalDate date;

    @NotNull(message = "Subject id is required.")
    @Schema(description = "Subject id", example = "5")
    private Long subjectId;
}
