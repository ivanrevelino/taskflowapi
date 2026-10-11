package com.ivan.taskflowapi.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AuthLoginDTO(
        @NotBlank
        @Schema(
                description = "This is the username to login",
                example = "vasquinho.md"
        )
        String username,

        @NotBlank
        String password
) {
}
