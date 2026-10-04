package com.ivan.taskflowapi.dto.task;

import com.ivan.taskflowapi.dto.task_comment.TaskCommentResponseDTO;
import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.models.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

public record TaskResponseDTO(
        Long id,
        String title,
        String description,
        TaskStatus status,
        List<TaskCommentResponseDTO> comments,
        LocalDateTime createdAt,
        UserResponseDTO assignee
) {}
