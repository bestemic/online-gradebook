package com.pawlik.przemek.onlinegradebook.dto.user;

import com.pawlik.przemek.onlinegradebook.dto.role.RoleDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
public class UserDto {

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;

    @Schema(description = "First name of the user", example = "John")
    private String firstName;

    @Schema(description = "Last name of the user", example = "Doe")
    private String lastName;

    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Phone number of the user", example = "1234567890")
    private String phoneNumber;

    @Schema(description = "Birth date of the user", example = "1990-01-01")
    private LocalDate birth;

    @Schema(description = "Roles assigned to the user")
    private Set<RoleDto> roles;
}
