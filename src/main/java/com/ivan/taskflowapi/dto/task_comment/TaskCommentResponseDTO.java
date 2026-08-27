package com.ivan.taskflowapi.dto.task_comment;

import com.ivan.taskflowapi.dto.user.UserResponseDTO;

import java.time.LocalDateTime;

public record TaskCommentResponseDTO(
        Long id,
        String content,
        UserResponseDTO user,
        Long taskId,
        LocalDateTime createdAt
) {
}
