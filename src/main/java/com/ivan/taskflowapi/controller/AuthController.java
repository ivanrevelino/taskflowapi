package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.auth.AuthLoginDTO;
import com.ivan.taskflowapi.dto.auth.AuthRegisterDTO;
import com.ivan.taskflowapi.dto.auth.LoginResponse;
import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.service.AuthService;
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
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid AuthLoginDTO dto) {

        LoginResponse loginResponse = authService.login(dto);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid AuthRegisterDTO dto){
        UserResponseDTO response = authService.register(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
