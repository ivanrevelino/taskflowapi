package com.ivan.taskflowapi.dto.task;

import com.ivan.taskflowapi.models.enums.TaskStatus;

public record TaskResponseDTO(
        Long id,
        String title,
        String description,
        TaskStatus status
) {
}
