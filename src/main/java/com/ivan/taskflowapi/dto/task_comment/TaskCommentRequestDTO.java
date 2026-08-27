package com.ivan.taskflowapi.dto.task_comment;

import jakarta.validation.constraints.NotBlank;

public record TaskCommentRequestDTO(
        @NotBlank String content
) {
}
