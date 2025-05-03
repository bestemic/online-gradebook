package com.pawlik.przemek.onlinegradebook.dto.user;

import com.pawlik.przemek.onlinegradebook.dto.role.GetRolesDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record GetUserDto(
        @Schema(description = "The unique identifier of the user.", example = "1")
        Long id,

        @Schema(description = "User's first name.", example = "John")
        String firstName,

        @Schema(description = "User's last name.", example = "Doe")
        String lastName,

        @Schema(description = "Email address used for user login and contact.", example = "john.doe@example.com", format = "email")
        String email,

        @Schema(description = "Phone number of the user. Expected format: 9 digits without spaces or separators.", example = "123456789")
        String phoneNumber,

        @Schema(description = "Date of birth of the user in 'yyyy-MM-dd' format.", example = "1990-01-01", format = "date")
        LocalDate birth,

        @Schema(description = "The unique identifier of the class the user is assigned to. Null if not assigned.", example = "3")
        Long assignedClassId,

        @Schema(description = "Roles assigned to the user")
        GetRolesDto roles
) {
}
