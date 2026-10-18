package com.ivan.taskflowapi.dto.project_member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request to add an existing user to a project.")
public record AddMemberRequestDTO(
        @Schema(description = "Identifier of the user to add.", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        @Positive Long id) {
}
