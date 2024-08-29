package com.pawlik.przemek.onlinegradebook.controller;

import com.pawlik.przemek.onlinegradebook.dto.error.ErrorResponseDto;
import com.pawlik.przemek.onlinegradebook.dto.error.ValidationErrorDto;
import com.pawlik.przemek.onlinegradebook.dto.school_class.SchoolClassAddDto;
import com.pawlik.przemek.onlinegradebook.dto.school_class.SchoolClassDto;
import com.pawlik.przemek.onlinegradebook.service.SchoolClassService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/classes")
@Tag(name = "Classes API", description = "Endpoints for managing classes")
public class SchoolClassController {

    private final SchoolClassService schoolClassService;

    public SchoolClassController(SchoolClassService schoolClassService) {
        this.schoolClassService = schoolClassService;
    }

    @Operation(summary = "Class creation", description = "Endpoint for class creation. Only users with role Admin can access this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Class created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SchoolClassDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<SchoolClassDto> createClass(@Valid @RequestBody SchoolClassAddDto schoolClassAddDto) {
        SchoolClassDto schoolClassDto = schoolClassService.createClass(schoolClassAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(schoolClassDto);
    }

    @Operation(summary = "Get all classes", description = "Endpoint for retrieving the list of all classes. Only users with role Admin can access this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classes retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SchoolClassDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping
    ResponseEntity<List<SchoolClassDto>> getAllClasses() {
        List<SchoolClassDto> classes = schoolClassService.getAllClasses();
        return ResponseEntity.ok().body(classes);
    }

    @Operation(summary = "Get class", description = "Endpoint for retrieving class info.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SchoolClassDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not Found - Class not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/{classId}")
    ResponseEntity<SchoolClassDto> getById(@PathVariable Long classId) {
        SchoolClassDto schoolClassDto = schoolClassService.getClassById(classId);
        return ResponseEntity.ok().body(schoolClassDto);
    }
}
