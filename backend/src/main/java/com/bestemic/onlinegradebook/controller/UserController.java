package com.bestemic.onlinegradebook.controller;

import com.bestemic.onlinegradebook.dto.*;
import com.bestemic.onlinegradebook.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/users")
@Tag(name = "Users API", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

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
}
