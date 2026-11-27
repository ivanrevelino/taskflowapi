package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.project_member.AddMemberRequestDTO;
import com.ivan.taskflowapi.dto.project_member.ChangePositionRequestDTO;
import com.ivan.taskflowapi.dto.project_member.ProjectMemberResponseDTO;
import com.ivan.taskflowapi.handler.RestErrorMessage;
import com.ivan.taskflowapi.service.ProjectMemberService;
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

@Tag(name = "Project members")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @PostMapping
    @Operation(summary = "Add project member", description = "Adds a user to the project. Only the project owner can perform this action.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Member added successfully"),
            @ApiResponse(responseCode = "400", description = "User is already a member or cannot be invited",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "User is not the project owner",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project or user not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<ProjectMemberResponseDTO> addMember(
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId,
            @RequestBody @Valid AddMemberRequestDTO request) {
        ProjectMemberResponseDTO projectMember = projectMemberService.addMember(projectId, request);
        return new ResponseEntity<>(projectMember, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List project members", description = "Returns all members of a project. The authenticated user must be a member.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Members retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Authenticated user is not a project member",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public Page<ProjectMemberResponseDTO> findAllMembers(
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId,
            @ParameterObject Pageable pageable) {
        return projectMemberService.findAll(projectId, pageable);
    }

    @DeleteMapping("/{memberId}")
    @Operation(summary = "Remove project member", description = "Removes a member from a project. Only the project owner can perform this action.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Member removed successfully"),
            @ApiResponse(responseCode = "403", description = "User is not the project owner",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project or member not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId,
            @Parameter(description = "User identifier of the member to remove", example = "2") @PathVariable Long memberId) {
        projectMemberService.delete(projectId, memberId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{memberId}/position")
    @Operation(summary = "Change project member role",
            description = "Changes a project member's role to ADMIN or MEMBER. Only the project owner can perform this action.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member role changed successfully"),
            @ApiResponse(responseCode = "400", description = "Owner cannot change their own role or role is invalid",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "User is not the project owner",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project or member not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ProjectMemberResponseDTO changePosition(
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId,
            @Parameter(description = "Membership identifier", example = "5") @PathVariable Long memberId,
            @RequestBody @Valid ChangePositionRequestDTO request) {
        return projectMemberService.changePosition(projectId, memberId, request.role());
    }

    @DeleteMapping("/leave")
    @Operation(summary = "Leave project", description = "Removes the authenticated user from a project. The owner cannot leave their own project.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Project left successfully"),
            @ApiResponse(responseCode = "400", description = "Project owner cannot leave their own project",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project or membership not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<Void> leaveProject(
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId) {
        projectMemberService.leaveProject(projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
