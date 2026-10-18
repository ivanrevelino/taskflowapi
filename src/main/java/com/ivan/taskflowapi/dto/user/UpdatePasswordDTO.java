package com.ivan.taskflowapi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Current and replacement passwords for the authenticated user.")
public record UpdatePasswordDTO (
        @Schema(description = "Current password.", example = "OldP@ssw0rd", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String oldPassword,
        @Schema(description = "New password.", example = "NewP@ssw0rd", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String newPassword) {
}
