package com.pawlik.przemek.onlinegradebook.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserIdsRequestDto {

    @NotEmpty(message = "User IDs list must not be empty.")
    @Schema(description = "List of user IDs", example = "[1, 2, 3]")
    private List<Long> userIds;
}
