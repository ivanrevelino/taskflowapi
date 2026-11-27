package com.ivan.taskflowapi.dto.project_member;

import com.ivan.taskflowapi.models.enums.ProjectMemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to change a project member's role.")
public record ChangePositionRequestDTO(
        @Schema(description = "New project role. Only ADMIN and MEMBER are allowed.", example = "ADMIN",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull ProjectMemberRole role) {
}
