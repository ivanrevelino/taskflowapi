package com.ivan.taskflowapi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "New username for the authenticated user.")
public record UpdateUsernameDTO(
        @Schema(description = "Username to set.", example = "newmember01", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String username
) {}
