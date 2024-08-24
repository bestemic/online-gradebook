package com.pawlik.przemek.onlinegradebook.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
public class RoleDto {

    @Schema(description = "Identifier of the role", example = "1")
    private Long id;

    @Schema(description = "Name of the role", example = "ROLE_ADMIN")
    private String name;
}




