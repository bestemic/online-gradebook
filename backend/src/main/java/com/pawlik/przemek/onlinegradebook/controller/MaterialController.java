package com.pawlik.przemek.onlinegradebook.controller;

import com.pawlik.przemek.onlinegradebook.dto.error.ErrorResponseDto;
import com.pawlik.przemek.onlinegradebook.dto.error.ValidationErrorDto;
import com.pawlik.przemek.onlinegradebook.dto.material.MaterialAddDto;
import com.pawlik.przemek.onlinegradebook.dto.material.MaterialDto;
import com.pawlik.przemek.onlinegradebook.service.MaterialService;
import com.pawlik.przemek.onlinegradebook.service.file.FileWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/materials")
@Tag(name = "Materials API", description = "Endpoints for managing materials")
@AllArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

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

    @Operation(summary = "Get list of materials by subject", description = "Retrieve a list of materials for a given subject ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Materials retrieved successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MaterialDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Subject not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<MaterialDto>> getMaterialsBySubject(@PathVariable Long subjectId) {
        List<MaterialDto> materials = materialService.getMaterialsBySubject(subjectId);
        return ResponseEntity.ok().body(materials);
    }

    @Operation(summary = "Get material file", description = "Download a file associated with a material")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully",
                    content = @Content(schema = @Schema(type = "string", format = "binary"))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "File not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Could not get the file",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("{materialId}/file")
    public ResponseEntity<Resource> getMaterialFile(@PathVariable Long materialId) {
        FileWrapper wrapper = materialService.getMaterialFile(materialId);

        String contentType;
        try {
            contentType = Files.probeContentType(Paths.get(wrapper.resource().getURI()));
        } catch (IOException ex) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.parseMediaType(contentType).toString());
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + wrapper.fileName());
        return ResponseEntity.ok().headers(headers).body(wrapper.resource());
    }
}
