package com.pawlik.przemek.onlinegradebook.controller;

import com.pawlik.przemek.onlinegradebook.dto.error.ErrorResponseDto;
import com.pawlik.przemek.onlinegradebook.dto.error.ValidationErrorDto;
import com.pawlik.przemek.onlinegradebook.dto.grade.GradeStudentDto;
import com.pawlik.przemek.onlinegradebook.dto.grade.GradesAddDto;
import com.pawlik.przemek.onlinegradebook.dto.grade.GradesDto;
import com.pawlik.przemek.onlinegradebook.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/grades")
@Tag(name = "Grades API", description = "Endpoints for managing grades")
@AllArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @Operation(summary = "Grades creation", description = "Endpoint for grades creation. Only users with role Teacher can access this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Grades created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GradesDto.class))
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
            @ApiResponse(responseCode = "404", description = "Not Found - Subject not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict - Grades already exists in this subject",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<GradesDto> create(@Valid @RequestBody GradesAddDto gradesAddDto) {
        GradesDto grades = gradeService.create(gradesAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(grades);
    }

    @Operation(summary = "Get grades by subject", description = "Fetch grades for a specific subject by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grades fetched successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GradesDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not Found - Subject or grades not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<GradesDto>> getGradesBySubject(@PathVariable Long subjectId) {
        List<GradesDto> grades = gradeService.getGradesBySubject(subjectId);
        return ResponseEntity.ok().body(grades);
    }

    @Operation(summary = "Get grades by subject and student", description = "Fetch grades for a specific subject and student by their IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grades fetched successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GradeStudentDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not Found - Subject or student or grades not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/subject/{subjectId}/student/{studentId}")
    public ResponseEntity<List<GradeStudentDto>> getGradesBySubjectAndStudent(@PathVariable Long subjectId, @PathVariable Long studentId) {
        List<GradeStudentDto> grades = gradeService.getGradesBySubjectAndStudent(subjectId, studentId);
        return ResponseEntity.ok().body(grades);
    }
}
