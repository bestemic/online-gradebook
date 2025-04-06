package com.pawlik.przemek.onlinegradebook.controller;

import com.pawlik.przemek.onlinegradebook.dto.error.ErrorResponseDto;
import com.pawlik.przemek.onlinegradebook.dto.role.GetRolesDto;
import com.pawlik.przemek.onlinegradebook.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/roles")
@Tag(name = "Roles API", description = "Endpoints for managing user roles within the system.")
@AllArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Retrieve all available roles", description = "Fetches a list of all user roles, which define the access levels and permissions within the system. Roles typically follow the naming convention 'ROLE_*'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles fetched successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetRolesDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping
    public ResponseEntity<GetRolesDto> getAllRoles() {
        var roles = roleService.getAllRoles();
        return ResponseEntity.ok().body(roles);
    }
}
