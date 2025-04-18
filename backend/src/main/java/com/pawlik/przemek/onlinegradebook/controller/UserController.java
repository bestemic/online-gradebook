package com.pawlik.przemek.onlinegradebook.controller;

import com.pawlik.przemek.onlinegradebook.dto.error.ErrorResponseDto;
import com.pawlik.przemek.onlinegradebook.dto.error.ValidationErrorDto;
import com.pawlik.przemek.onlinegradebook.dto.password.ChangePasswordDto;
import com.pawlik.przemek.onlinegradebook.dto.password.PasswordDto;
import com.pawlik.przemek.onlinegradebook.dto.token.TokenDto;
import com.pawlik.przemek.onlinegradebook.dto.user.UserAddDto;
import com.pawlik.przemek.onlinegradebook.dto.user.UserDto;
import com.pawlik.przemek.onlinegradebook.dto.user.UserIdsRequestDto;
import com.pawlik.przemek.onlinegradebook.dto.user.UserLoginDto;
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

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/users")
@Tag(name = "Users API", description = "Endpoints for managing users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "User login", description = "Endpoint for user authentication and JWT token generation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login operation",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<TokenDto> loginUser(@Valid @RequestBody UserLoginDto userLoginDto) {
        String jwt = userService.authenticateAndGenerateToken(userLoginDto);
        return ResponseEntity.ok().body(new TokenDto(jwt));
    }

    @Operation(summary = "User creation", description = "Endpoint for user creation and password generation. Only users with role Admin can access this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PasswordDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request",
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
    public ResponseEntity<PasswordDto> createUser(@Valid @RequestBody UserAddDto userAddDto) {
        String password = userService.createUser(userAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PasswordDto(password));
    }

    @Operation(summary = "Password change", description = "Endpoint for password changing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
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
    public ResponseEntity<Void> changePassword(@Parameter(description = "User id", required = true) @PathVariable Long userId, @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        userService.changePassword(userId, changePasswordDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Single user password reset", description = "Endpoint for user password reset.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PasswordDto.class))
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
    public ResponseEntity<PasswordDto> resetPassword(@Parameter(description = "User id", required = true) @PathVariable Long userId) {
        String password = userService.resetPassword(userId);
        return ResponseEntity.ok().body(new PasswordDto(password));
    }

    @Operation(summary = "Multiple users password reset", description = "Endpoint for multiple users password reset.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully",
                    content = @Content(mediaType = "application/pdf", schema = @Schema(type = "string", format = "binary"))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request",
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
    @PostMapping("/password/reset/bulk")
    public ResponseEntity<byte[]> resetMultiplePasswords(@Valid @RequestBody UserIdsRequestDto userIds) {
        byte[] pdfBytes = userService.resetPasswords(userIds.getUserIds());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=passwords_data.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @Operation(summary = "Get all users with given role", description = "Endpoint for retrieving the list of users with given role. When role is not specified all users are returned.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(
            @Parameter(
                    name = "roleName",
                    description = "User role name",
                    example = "ROLE_STUDENT"
            )
            @RequestParam(required = false) String roleName) {
        List<UserDto> users = userService.getAllUsers(roleName);
        return ResponseEntity.ok().body(users);
    }

    @Operation(summary = "Get user by ID", description = "Endpoint for retrieving a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not Found - User does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        UserDto user = userService.getUserById(userId);
        return ResponseEntity.ok().body(user);
    }
}
