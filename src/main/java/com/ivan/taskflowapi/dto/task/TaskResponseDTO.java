package com.ivan.taskflowapi.dto.task;

import com.ivan.taskflowapi.dto.task_comment.TaskCommentResponseDTO;
import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.models.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Task details including status, comments, and assignee.")
public record TaskResponseDTO(
        @Schema(description = "Task identifier.", example = "10")
        Long id,
        @Schema(description = "Task title.", example = "Implement login page")
        String title,
        @Schema(description = "Task description.", example = "Build and test the responsive login page.")
        String description,
        @Schema(description = "Current task status.", example = "TO_DO")
        TaskStatus status,
        @Schema(description = "Comments on the task.")
        List<TaskCommentResponseDTO> comments,
        @Schema(description = "Task creation date and time.", example = "2025-01-15T09:30:00")
        LocalDateTime createdAt,
        @Schema(description = "User assigned to the task.")
        UserResponseDTO assignee
) {}
