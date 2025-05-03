package com.pawlik.przemek.onlinegradebook.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record GetUserBasicDto(
        @Schema(description = "The unique identifier of the user.", example = "1")
        Long id,

        @Schema(description = "User's first name.", example = "John")
        String firstName,

        @Schema(description = "User's last name.", example = "Doe")
        String lastName,

        @Schema(description = "Email address used for user login and contact.", example = "john.doe@example.com", format = "email")
        String email
) {
}
