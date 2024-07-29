package com.bestemic.onlinegradebook.controller;

import com.bestemic.onlinegradebook.dto.ErrorResponseDto;
import com.bestemic.onlinegradebook.dto.ValidationErrorDto;
import com.bestemic.onlinegradebook.dto.class_group.ClassGroupAddDto;
import com.bestemic.onlinegradebook.dto.class_group.ClassGroupDto;
import com.bestemic.onlinegradebook.service.ClassGroupService;
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
public class ClassGroupController {

    private final ClassGroupService classGroupService;

    public ClassGroupController(ClassGroupService classGroupService) {
        this.classGroupService = classGroupService;
    }

    @Operation(summary = "Class creation", description = "Endpoint for class creation. Only users with role Admin can access this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Class created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClassGroupDto.class))
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
    public ResponseEntity<ClassGroupDto> createClass(@Valid @RequestBody ClassGroupAddDto classGroupAddDto) {
        ClassGroupDto classGroupDto = classGroupService.createClass(classGroupAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(classGroupDto);
    }

    @Operation(summary = "Get all classes", description = "Endpoint for retrieving the list of all classes. Only users with role Admin can access this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classes retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ClassGroupDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping
    ResponseEntity<List<ClassGroupDto>> getAllClasses() {
        List<ClassGroupDto> classes = classGroupService.getAllClasses();
        return ResponseEntity.ok().body(classes);
    }
}
