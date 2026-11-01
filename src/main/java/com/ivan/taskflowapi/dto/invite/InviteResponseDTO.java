package com.ivan.taskflowapi.dto.invite;

import com.ivan.taskflowapi.models.enums.InviteStatus;

import java.time.LocalDateTime;

public record InviteResponseDTO(
        Long id,
        Long projectId,
        Long invitedId,
        Long inviterId,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        InviteStatus status,
        String message) {
}
