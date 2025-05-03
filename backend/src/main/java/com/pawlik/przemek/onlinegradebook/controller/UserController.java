package com.pawlik.przemek.onlinegradebook.controller;

import com.pawlik.przemek.onlinegradebook.dto.error.ErrorResponseDto;
import com.pawlik.przemek.onlinegradebook.dto.error.ValidationErrorDto;
import com.pawlik.przemek.onlinegradebook.dto.password.ChangePasswordDto;
import com.pawlik.przemek.onlinegradebook.dto.password.GetPasswordDto;
import com.pawlik.przemek.onlinegradebook.dto.password.ResetUserPasswordsRequestDto;
import com.pawlik.przemek.onlinegradebook.dto.token.GetTokenDto;
import com.pawlik.przemek.onlinegradebook.dto.user.AddUserDto;
import com.pawlik.przemek.onlinegradebook.dto.user.GetUserDto;
import com.pawlik.przemek.onlinegradebook.dto.user.GetUsersDto;
import com.pawlik.przemek.onlinegradebook.dto.user.LoginUserDto;
import com.pawlik.przemek.onlinegradebook.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/users")
@Tag(name = "Users API", description = "Endpoints for managing users, including authentication, creation, retrieval, and password operations.")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Authenticate user and generate token",
            description = "Authenticates a user with provided credentials and returns a JWT token on success."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetTokenDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<GetTokenDto> loginUser(@Valid @RequestBody LoginUserDto loginUserDto) {
        var jwt = userService.loginUser(loginUserDto);
        return ResponseEntity.ok().body(jwt);
    }

    @Operation(
            summary = "Create new user",
            description = "Creates a new user and returns a system-generated password. Accessible only to users with the ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetPasswordDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<GetPasswordDto> addUser(@Valid @RequestBody AddUserDto addUserDto) {
        var password = userService.addUser(addUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(password);
    }

    @Operation(
            summary = "Change password",
            description = "Changes the password for a specific user identified by their identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "ID of the user whose password is to be changed", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordDto changePasswordDto
    ) {
        userService.changePassword(userId, changePasswordDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Reset single user's password",
            description = "Resets the password of a specified user and returns the new password."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetPasswordDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/{userId}/password/reset")
    public ResponseEntity<GetPasswordDto> resetPassword(
            @Parameter(description = "ID of the user whose password will be reset", required = true)
            @PathVariable Long userId
    ) {
        var password = userService.resetPassword(userId);
        return ResponseEntity.ok().body(password);
    }

    @Operation(
            summary = "Bulk password reset for multiple users",
            description = "Resets passwords for multiple users specified by their IDs. Returns a downloadable PDF containing the new passwords. This operation is restricted to users with administrative privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully. The response contains a PDF file with newly generated passwords.",
                    content = @Content(mediaType = "application/pdf", schema = @Schema(type = "string", format = "binary"))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "One or more users not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/password/reset/bulk")
    public ResponseEntity<byte[]> resetPasswordsInBulk(@Valid @RequestBody ResetUserPasswordsRequestDto resetUserPasswordsRequestDto) {
        var pdfBytes = userService.resetPasswords(resetUserPasswordsRequestDto.userIds());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=passwords_data.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @Operation(
            summary = "Get users by role",
            description = "Retrieves a list of users that have the specified role. If no role is provided, all users are returned."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users fetched successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetUsersDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping
    public ResponseEntity<GetUsersDto> getUsersByRole(
            @Parameter(
                    name = "roleName",
                    description = "Role name used to filter users (e.g., ROLE_TEACHER, ROLE_ADMIN). Leave empty to retrieve all users.",
                    example = "ROLE_STUDENT"
            )
            @RequestParam(required = false) String roleName
    ) {
        var users = userService.getUsersByRole(roleName);
        return ResponseEntity.ok().body(users);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Fetches detailed information about a user identified by their unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetUserDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/{userId}")
    public ResponseEntity<GetUserDto> getUserById(
            @Parameter(description = "Unique identifier of the user", required = true)
            @PathVariable Long userId
    ) {
        var user = userService.getUserById(userId);
        return ResponseEntity.ok().body(user);
    }
}
