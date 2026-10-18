package com.ivan.taskflowapi.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication tokens returned by login and refresh operations.")
public record AuthResponse(
        @Schema(description = "Bearer access token.", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken,
        @Schema(description = "Refresh token used to obtain a new access token.", example = "eyJhbGciOiJIUzI1NiJ9...")
        String refreshToken) {
}
