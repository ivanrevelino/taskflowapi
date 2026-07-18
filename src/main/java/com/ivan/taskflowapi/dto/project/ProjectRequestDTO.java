package com.ivan.taskflowapi.dto.project;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequestDTO (
        @NotBlank String name,
        @NotBlank String description
){}
