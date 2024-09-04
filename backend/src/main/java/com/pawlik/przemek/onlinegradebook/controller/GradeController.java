package com.pawlik.przemek.onlinegradebook.controller;

import com.pawlik.przemek.onlinegradebook.dto.error.ErrorResponseDto;
import com.pawlik.przemek.onlinegradebook.dto.error.ValidationErrorDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/grades")
@Tag(name = "Grades API", description = "Endpoints for managing grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

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
            @ApiResponse(responseCode = "409", description = "Conflict - Grades already exists in this class",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<GradesDto> create(@Valid @RequestBody GradesAddDto gradesAddDto) {
        GradesDto grades = gradeService.create(gradesAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(grades);
    }
}
