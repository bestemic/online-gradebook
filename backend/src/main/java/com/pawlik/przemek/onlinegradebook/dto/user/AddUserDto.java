package com.pawlik.przemek.onlinegradebook.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public record AddUserDto(
        @NotBlank(message = "First name must not be blank.")
        @Size(min = 2, max = 50, message = "First name length must be between 2 and 50 characters.")
        @Schema(description = "First name of the user. Must be between 2 and 50 characters.", example = "John")
        String firstName,

        @NotBlank(message = "Last name must not be blank.")
        @Size(min = 2, max = 50, message = "Last name length must be between 2 and 50 characters.")
        @Schema(description = "Last name of the user. Must be between 2 and 50 characters.", example = "Doe")
        String lastName,

        @NotBlank(message = "Email must not be blank.")
        @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message = "Invalid email format.")
        @Schema(description = "Email address used for login. Must follow standard email format.", example = "john.doe@example.com", format = "email")
        String email,

        @Pattern(regexp = "^\\d{9}$", message = "Phone number must be exactly 9 digits.")
        @Schema(description = "Phone number of the user. Required for roles: ADMIN, TEACHER. Must be exactly 9 digits.", example = "123456789")
        String phoneNumber,

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "Date of birth of the user. Required for role: STUDENT. Format: yyyy-MM-dd.", example = "2000-01-01", format = "date")
        LocalDate birth,

        @NotEmpty(message = "At least one role must be selected.")
        @Schema(description = "List of role IDs to assign to the user. Roles influence which fields are required (e.g., STUDENT requires birth date).", example = "[1, 2]")
        List<Long> roleIds
) {
}
