package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.auth.AuthLoginDTO;
import com.ivan.taskflowapi.dto.auth.AuthRegisterDTO;
import com.ivan.taskflowapi.dto.auth.LoginResponse;
import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "When successful")
    })
    @Operation(summary = "Login", description = "Make's login and return a token JWT")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid AuthLoginDTO dto) {

        LoginResponse loginResponse = authService.login(dto);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "When successful"),
            @ApiResponse(responseCode = "400", description = "When user already exists")
    })
    @Operation(summary = "Register a new User", description = "Register a new user in the system")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid AuthRegisterDTO dto){
        UserResponseDTO response = authService.register(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
