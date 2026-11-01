package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.invite.InviteRequestDTO;
import com.ivan.taskflowapi.dto.invite.InviteResponseDTO;
import com.ivan.taskflowapi.handler.RestErrorMessage;
import com.ivan.taskflowapi.models.enums.InviteStatus;
import com.ivan.taskflowapi.service.ProjectInviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Project invites")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
public class ProjectInviteController {

    private final ProjectInviteService projectInviteService;

    @GetMapping("/me/invites")
    @Operation(summary = "List received invitations", description = "Returns a paginated list of invitations received by the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Invitations retrieved successfully")
    public Page<InviteResponseDTO> findMyInvites(@ParameterObject Pageable pageable) {
        return projectInviteService.findMyInvites(pageable);
    }

    @GetMapping("/projects/{projectId}/invites")
    @Operation(summary = "List project invitations", description = "Returns project invitations optionally filtered by status. Only project owners and administrators can perform this action.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invitations retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "User is not a project owner or administrator",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public Page<InviteResponseDTO> findProjectInvites(
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId,
            @Parameter(description = "Filter by invitation status", example = "PENDING")
            @RequestParam(required = false) InviteStatus status,
            @ParameterObject Pageable pageable) {
        return projectInviteService.findProjectInvites(projectId, status, pageable);
    }

    @PostMapping("/projects/{projectId}/invites")
    @Operation(summary = "Invite a user to a project", description = "Only project owners and administrators can invite users.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Invitation created successfully"),
            @ApiResponse(responseCode = "400", description = "User cannot be invited",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "User is not a project owner or administrator",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project or user not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<InviteResponseDTO> invite(
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId,
            @RequestBody @Valid InviteRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectInviteService.invite(projectId, request.invitedId(), request.message()));
    }

    @PatchMapping("/me/invites/{inviteId}/accept")
    @Operation(summary = "Accept a project invitation")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Invitation accepted successfully"),
            @ApiResponse(responseCode = "400", description = "Invitation is expired or is not pending",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Invitation does not belong to the authenticated user",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Invitation not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<Void> accept(
            @Parameter(description = "Invitation identifier", example = "1") @PathVariable Long inviteId) {
        projectInviteService.accept(inviteId);
        return ResponseEntity.noContent().build();
    }
}
