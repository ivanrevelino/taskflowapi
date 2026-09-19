package com.ivan.taskflowapi.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UpdateUsernameDTO(
        @NotBlank String username
) {}
