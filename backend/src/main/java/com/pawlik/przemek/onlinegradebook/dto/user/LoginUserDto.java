package com.pawlik.przemek.onlinegradebook.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginUserDto(
        @NotBlank(message = "Email must not be blank.")
        @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message = "Invalid email format.")
        @Schema(description = "Email address used for login.", example = "user@user.com", format = "email")
        String email,

        @NotBlank(message = "Password must not be blank.")
        @Size(min = 4, message = "Password must be at least 4 characters long.")
        @Schema(description = "User's login password. Minimum length is 4 characters.", example = "test123")
        String password
) {
}
