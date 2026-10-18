package com.ivan.taskflowapi.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Information required to create a user account.")
public record AuthRegisterDTO(
        @Size(min = 6, max = 16)
        @Schema(description = "Unique username. Must contain 6 to 16 characters.", example = "member01",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String username,

        @Size(max = 20)
        @Schema(description = "Display name. Maximum 20 characters.", example = "Sam Member",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String name,

        @Schema(description = "Account password. Must contain 8 to 16 characters.", example = "P@ssw0rd",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(min = 8, max = 16)
        @NotBlank String password) {
}
