package com.ivan.taskflowapi.dto.project;

import com.ivan.taskflowapi.models.enums.ProjectMemberRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Project summary including the authenticated user's role.")
public record ProjectSummaryResponse(
        @Schema(description = "Project identifier.", example = "1") Long id,
        @Schema(description = "Project name.", example = "Website redesign") String name,
        @Schema(description = "Project description.", example = "Plan and deliver the new company website.") String description,
        @Schema(description = "Authenticated user's role in the project.", example = "OWNER") ProjectMemberRole role) {
}
