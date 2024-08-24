package com.pawlik.przemek.onlinegradebook.dto.password;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PasswordDto {

    @Schema(description = "Generated password", example = "generated_password")
    private String password;
}