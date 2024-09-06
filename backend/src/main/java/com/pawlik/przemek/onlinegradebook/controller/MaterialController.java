package com.pawlik.przemek.onlinegradebook.controller;

import com.pawlik.przemek.onlinegradebook.dto.error.ErrorResponseDto;
import com.pawlik.przemek.onlinegradebook.dto.error.ValidationErrorDto;
import com.pawlik.przemek.onlinegradebook.dto.material.MaterialAddDto;
import com.pawlik.przemek.onlinegradebook.dto.material.MaterialDto;
import com.pawlik.przemek.onlinegradebook.service.MaterialService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/api/v1/materials")
@Tag(name = "Materials API", description = "Endpoints for managing materials")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @Operation(summary = "Create a new material", description = "Endpoint to create a new material and upload a file associated with it. Max file size is 2MB.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Material created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MaterialDto.class))
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
            @ApiResponse(responseCode = "404", description = "Not Found - Subject does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Could not store the file",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MaterialDto> createMaterial(
            @Valid @ModelAttribute @Parameter(description = "DTO containing material details", required = true) MaterialAddDto materialAddDto,
            @RequestParam("file") @Parameter(description = "File to be uploaded", required = true) MultipartFile file) {
        MaterialDto material = materialService.createMaterial(materialAddDto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(material);
    }
}
