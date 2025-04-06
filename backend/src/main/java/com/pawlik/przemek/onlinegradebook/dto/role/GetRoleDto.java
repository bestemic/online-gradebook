package com.pawlik.przemek.onlinegradebook.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetRoleDto(
        @Schema(description = "The unique identifier of the role", example = "1")
        Long id,

        @Schema(description = "Role name assigned to the user start with 'ROLE_' prefix (e.g., ROLE_STUDENT)", example = "ROLE_ADMIN")
        String name
) {
}
