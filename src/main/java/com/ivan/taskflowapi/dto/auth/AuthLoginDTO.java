package com.ivan.taskflowapi.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginDTO(@NotBlank String username, @NotBlank String password) {
}
