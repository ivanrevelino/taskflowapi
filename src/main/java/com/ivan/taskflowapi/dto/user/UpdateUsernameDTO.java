package com.ivan.taskflowapi.dto.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdateUsernameDTO(
        @NotBlank
        @Min(6)
        String username
) {}
