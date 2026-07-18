package com.ivan.taskflowapi.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthRegisterDTO(@NotBlank String username, @NotBlank String name,  @NotBlank String password) {
}
