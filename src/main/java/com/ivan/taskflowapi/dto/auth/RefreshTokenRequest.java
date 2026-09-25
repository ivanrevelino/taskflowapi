package com.ivan.taskflowapi.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(@NotBlank(message = "Token cannot be null or empty") String token) {
}
