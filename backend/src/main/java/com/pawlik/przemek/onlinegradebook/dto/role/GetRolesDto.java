package com.pawlik.przemek.onlinegradebook.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record GetRolesDto(
        @Schema(description = "A list of user roles. Each role defines a specific set of permissions and access levels within the application.")
        List<GetRoleDto> roles
) {
}
