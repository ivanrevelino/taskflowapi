package com.ivan.taskflowapi.dto.task;

import jakarta.validation.constraints.NotBlank;

public record TaskRequestDTO(
        @NotBlank String title,
        @NotBlank String description
) {}
