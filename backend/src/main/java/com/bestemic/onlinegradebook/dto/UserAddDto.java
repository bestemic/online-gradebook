package com.bestemic.onlinegradebook.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UserAddDto {

    @NotNull(message = "First name is required.")
    @NotBlank(message = "First name must not be blank.")
    @Size(min = 2, max = 50, message = "First name length must be between 2 and 50 characters.")
    @Schema(description = "User first name", example = "John")
    private String firstName;

    @NotNull(message = "Last name is required.")
    @NotBlank(message = "Last name must not be blank.")
    @Size(min = 2, max = 50, message = "Last name length must be between 2 and 50 characters.")
    @Schema(description = "User last name", example = "Doe")
    private String lastName;

    @NotNull(message = "Email is required.")
    @NotBlank(message = "Email must not be blank.")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message = "Invalid email format.")
    @Schema(description = "User email", example = "user@user.com")
    private String email;

    @Pattern(regexp = "^\\d{9}$", message = "Invalid phone number format. It must be 9 digits.")
    @Schema(description = "User phone number", example = "123456789")
    private String phoneNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "User date of birth", example = "2000-01-01")
    private LocalDate birth;

    @NotEmpty(message = "At least one role is required.")
    @Schema(description = "User role IDs", example = "[1, 2]")
    private List<Long> roleIds;
}
