package com.ivan.taskflowapi.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credentials used to authenticate a user.")
public record AuthLoginDTO(
        @NotBlank
        @Schema(
                description = "Username used to sign in.",
                example = "member01",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String username,

        @Schema(description = "User password.", example = "P@ssw0rd", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String password
) {
}
