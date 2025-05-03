package com.pawlik.przemek.onlinegradebook.dto.password;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record ChangePasswordDto(

        @NotBlank(message = "Current password must not be blank.")
        @Size(min = 4, message = "Current password must be at least 4 characters long.")
        @Schema(description = "The user's current password. Required to authenticate the password change request.", example = "currentPass123")
        String currentPassword,

        @NotBlank(message = "New password must not be blank.")
        @Size(min = 4, message = "New password must be at least 4 characters long.")
        @Schema(description = "The new password that the user wants to set. Must meet minimum length requirements.", example = "newSecurePass456")
        String newPassword
) {
}
