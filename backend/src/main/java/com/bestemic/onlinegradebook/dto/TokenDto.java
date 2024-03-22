package com.bestemic.onlinegradebook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class TokenDto {

    @Schema(description = "Generated JSON Web Token", example = "generated_token")
    private String token;
}
