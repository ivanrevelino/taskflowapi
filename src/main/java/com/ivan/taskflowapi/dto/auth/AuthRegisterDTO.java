package com.ivan.taskflowapi.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRegisterDTO(
        @Size(min = 6, max = 16)
        @Schema(description = "This is the username of user, used to register", example = "@satoru.gojo")
        @NotBlank String username,

        @Size(max = 20)
        @Schema(description = "This is the name of user", example = "Satoru Gojo")
        @NotBlank String name,

        @Size(min = 8, max = 16)
        @NotBlank String password) {
}
