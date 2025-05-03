package com.pawlik.przemek.onlinegradebook.dto.password;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ResetUserPasswordsRequestDto(
        @NotEmpty(message = "User identifiers list cannot be empty.")
        @Schema(description = "A list of user identifiers whose passwords should be reset. Each identifier must correspond to an existing user.", example = "[1, 2, 3]")
        List<Long> userIds
) {
}
