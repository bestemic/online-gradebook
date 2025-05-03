package com.pawlik.przemek.onlinegradebook.dto.password;

import io.swagger.v3.oas.annotations.media.Schema;


public record GetPasswordDto(
        @Schema(description = "System-generated password for the newly created or reset user account. This password should be changed upon first login.", example = "Xy7!a92B")
        String password
) {
}
