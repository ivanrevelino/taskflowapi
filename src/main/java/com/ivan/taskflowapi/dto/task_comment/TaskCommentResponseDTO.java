package com.ivan.taskflowapi.dto.task_comment;

import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Task comment or reply.")
public record TaskCommentResponseDTO(
        @Schema(description = "Comment identifier.", example = "20")
        Long id,
        @Schema(description = "Comment content.", example = "I have started working on this.")
        String content,
        @Schema(description = "Comment author.")
        UserResponseDTO user,
        @Schema(description = "Identifier of the task this comment belongs to.", example = "10")
        Long taskId,
        @Schema(description = "Parent comment identifier for replies.", example = "19")
        Long parentCommentId,
        @Schema(description = "Comment creation date and time.", example = "2025-01-15T09:30:00")
        LocalDateTime createdAt
) {
}
