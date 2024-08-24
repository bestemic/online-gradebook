package com.pawlik.przemek.onlinegradebook.dto.password;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {

    @NotNull(message = "Current password is required.")
    @NotBlank(message = "Current password must not be blank.")
    @Size(min = 4, message = "Current password must be at least 4 characters long.")
    @Schema(description = "Current user password", example = "test123")
    private String currentPassword;

    @NotNull(message = "New password is required.")
    @NotBlank(message = "New password must not be blank.")
    @Size(min = 4, message = "New password must be at least 4 characters long.")
    @Schema(description = "New user password", example = "test123")
    private String newPassword;
}