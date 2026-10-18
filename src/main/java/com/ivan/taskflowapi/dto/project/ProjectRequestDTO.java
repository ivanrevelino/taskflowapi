package com.ivan.taskflowapi.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Project details supplied when creating or updating a project.")
public record ProjectRequestDTO (
        @Schema(description = "Project name.", example = "Website redesign", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String name,
        @Schema(description = "Project description.", example = "Plan and deliver the new company website.",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String description
){}
