package com.ivan.taskflowapi.dto.user;

import com.ivan.taskflowapi.models.enums.UserRoles;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserResponseDTO (
        @NotBlank Long id,
        @NotBlank String name,
        @NotBlank String username,
        @NotBlank UserRoles role
){}
