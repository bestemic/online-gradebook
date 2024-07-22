package com.bestemic.onlinegradebook.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDto {

    @NotNull(message = "Email is required.")
    @NotBlank(message = "Email must not be blank.")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message = "Invalid email format.")
    @Schema(description = "User email", example = "user@user.com")
    private String email;

    @NotNull(message = "Password is required.")
    @NotBlank(message = "Password must not be blank.")
    @Size(min = 4, message = "Password must be at least 4 characters long.")
    @Schema(description = "User password", example = "test123")
    private String password;
}
