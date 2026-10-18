package com.ivan.taskflowapi.dto.task_comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Comment text.")
public record TaskCommentRequestDTO(
        @Schema(description = "Comment content.", example = "I have started working on this.",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String content
) {
}
