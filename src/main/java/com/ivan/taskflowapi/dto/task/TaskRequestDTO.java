package com.ivan.taskflowapi.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Task title and description.")
public record TaskRequestDTO(
        @Schema(description = "Task title.", example = "Implement login page", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String title,
        @Schema(description = "Task description.", example = "Build and test the responsive login page.",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String description
) {}
