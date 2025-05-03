package com.pawlik.przemek.onlinegradebook.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record GetUsersDto(
        @Schema(description = "A list of users")
        List<GetUserDto> users
) {
}
