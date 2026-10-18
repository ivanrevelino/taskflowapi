package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.auth.*;
import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.handler.RestErrorMessage;
import com.ivan.taskflowapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and returns access and refresh tokens.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "400", description = "Request body is invalid",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Username or password is incorrect")
    })
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthLoginDTO dto) {

        AuthResponse loginResponse = authService.login(dto);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Creates a user account.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Request is invalid or username already exists",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid AuthRegisterDTO dto){
        UserResponseDTO response = authService.register(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Validates a refresh token and returns a new access token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Request body is invalid",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "Refresh token is unknown or expired")
    })
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
