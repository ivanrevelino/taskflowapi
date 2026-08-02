package com.ivan.taskflowapi.dto.project_member;

import jakarta.validation.constraints.Positive;

public record AddMemberRequestDTO(@Positive Long id) {
}
