package com.ivan.taskflowapi.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordDTO (@NotBlank String oldPassword, @NotBlank String newPassword){}
