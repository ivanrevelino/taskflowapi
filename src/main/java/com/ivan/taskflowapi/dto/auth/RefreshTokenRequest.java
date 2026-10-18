package com.ivan.taskflowapi.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Refresh token used to issue a new access token.")
public record RefreshTokenRequest(
        @Schema(description = "Previously issued refresh token.", example = "eyJhbGciOiJIUzI1NiJ9...",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Token cannot be null or empty") String token) {
}
