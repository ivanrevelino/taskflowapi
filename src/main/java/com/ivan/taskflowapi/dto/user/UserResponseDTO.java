package com.ivan.taskflowapi.dto.user;

import com.ivan.taskflowapi.models.enums.UserRoles;
import jakarta.validation.constraints.NotBlank;

public record UserResponseDTO (
        @NotBlank String name,
        @NotBlank String username,
        @NotBlank UserRoles role
){}
