package com.ivan.taskflowapi.dto.invite;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request to invite an existing user to a project.")
public record InviteRequestDTO(
        @Schema(description = "Identifier of the user to invite.", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Positive Long invitedId,
        @Schema(description = "Optional message to include with the invitation.", example = "Join our project")
        String message) {
}
