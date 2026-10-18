package com.ivan.taskflowapi.models.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Lifecycle status of a task.")
public enum TaskStatus {

    TO_DO,
    IN_PROGRESS,
    COMPLETED
}
