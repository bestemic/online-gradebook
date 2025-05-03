package com.pawlik.przemek.onlinegradebook.dto.token;

import io.swagger.v3.oas.annotations.media.Schema;


public record GetTokenDto(
        @Schema(description = "Generated JSON Web Token (JWT) used for authenticating subsequent requests. This token should be included in the Authorization header using the Bearer scheme.", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {
}
