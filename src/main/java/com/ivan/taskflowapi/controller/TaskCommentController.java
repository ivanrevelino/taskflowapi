package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.task_comment.TaskCommentRequestDTO;
import com.ivan.taskflowapi.dto.task_comment.TaskCommentResponseDTO;
import com.ivan.taskflowapi.handler.RestErrorMessage;
import com.ivan.taskflowapi.service.TaskCommentService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Task comments")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/tasks/{taskId}/comments")
public class TaskCommentController {

    private final TaskCommentService taskCommentService;

    @PostMapping
    @Operation(summary = "Create task comment", description = "Creates a comment on a task in a project.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Comment created successfully"),
            @ApiResponse(responseCode = "400", description = "Comment content is invalid",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "User is not a project member or task is outside the project",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project, task, or membership not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<TaskCommentResponseDTO> create(
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId,
            @Parameter(description = "Task identifier", example = "2") @PathVariable Long taskId,
            @RequestBody @Valid TaskCommentRequestDTO request
    ) {
        TaskCommentResponseDTO comment = taskCommentService.create(projectId, taskId, request);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @PostMapping("/{commentId}/reply")
    @Operation(summary = "Reply to a comment", description = "Creates a reply to an existing comment on a task.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reply created successfully"),
            @ApiResponse(responseCode = "400", description = "Reply content is invalid",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Task does not belong to the project",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project, task, or parent comment not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<TaskCommentResponseDTO> reply(
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId,
            @Parameter(description = "Task identifier", example = "2") @PathVariable Long taskId,
            @Parameter(description = "Parent comment identifier", example = "3") @PathVariable Long commentId,
            @RequestBody @Valid TaskCommentRequestDTO request
    ) {
        TaskCommentResponseDTO comment = taskCommentService.reply(projectId, taskId, commentId, request);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List task comments", description = "Returns comments for a task in chronological order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "User is not a project member or task is outside the project",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project, task, or membership not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<List<TaskCommentResponseDTO>> findAll(
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId,
            @Parameter(description = "Task identifier", example = "2") @PathVariable Long taskId
    ) {
        List<TaskCommentResponseDTO> comments = taskCommentService.findAll(projectId, taskId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "Update task comment", description = "Updates a comment authored by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment updated successfully"),
            @ApiResponse(responseCode = "400", description = "Comment content is invalid",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "User is not the comment author",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project, task, comment, or membership not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<TaskCommentResponseDTO> update(
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId,
            @Parameter(description = "Task identifier", example = "2") @PathVariable Long taskId,
            @Parameter(description = "Comment identifier", example = "3") @PathVariable Long commentId,
            @RequestBody @Valid TaskCommentRequestDTO request
    ) {
        TaskCommentResponseDTO comment = taskCommentService.update(projectId, taskId, commentId, request);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete task comment", description = "Deletes a comment authored by the authenticated user or by a project administrator.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to delete this comment",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project, task, comment, or membership not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId,
            @Parameter(description = "Task identifier", example = "2") @PathVariable Long taskId,
            @Parameter(description = "Comment identifier", example = "3") @PathVariable Long commentId
    ) {
        taskCommentService.delete(projectId, taskId, commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
