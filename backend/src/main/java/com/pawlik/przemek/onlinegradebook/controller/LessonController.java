package com.pawlik.przemek.onlinegradebook.controller;

import com.pawlik.przemek.onlinegradebook.dto.error.ErrorResponseDto;
import com.pawlik.przemek.onlinegradebook.dto.error.ValidationErrorDto;
import com.pawlik.przemek.onlinegradebook.dto.lesson.LessonAddDto;
import com.pawlik.przemek.onlinegradebook.dto.lesson.LessonDto;
import com.pawlik.przemek.onlinegradebook.dto.user.UserDto;
import com.pawlik.przemek.onlinegradebook.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/lessons")
@Tag(name = "Lessons API", description = "Endpoints for managing lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Operation(summary = "Create lesson", description = "Endpoint for lesson creation. Only users with role Teacher can access this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lesson created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LessonDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not Found - Class-Subject connection does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<LessonDto> createLesson(@Valid @RequestBody LessonAddDto lessonAddDto) {
        LessonDto lessonDto = lessonService.createLesson(lessonAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonDto);
    }

    @Operation(summary = "Get lesson by ID", description = "Endpoint for retrieving a lesson by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lesson retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LessonDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not Found - Lesson not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonDto> getLessonById(@PathVariable Long lessonId) {
        LessonDto lessonDto = lessonService.getLessonById(lessonId);
        return ResponseEntity.ok().body(lessonDto);
    }

    @Operation(summary = "Get all lessons with given class-subject-teacher connection", description = "Endpoint for retrieving the list of lessons from specific subject in class.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classes retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<LessonDto>> getAllLessons(
            @Parameter(

                    name = "classGroupSubjectTeacherId",
                    description = "Connection id between class, subject and teacher",
                    example = "3"
            )
            @RequestParam(required = false) Long classGroupSubjectTeacherId) {
        List<LessonDto> lessons = lessonService.getAllLessons(classGroupSubjectTeacherId);
        return ResponseEntity.ok().body(lessons);
    }
}