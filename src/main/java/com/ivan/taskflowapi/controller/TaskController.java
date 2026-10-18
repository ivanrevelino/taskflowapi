package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.task.TaskRequestDTO;
import com.ivan.taskflowapi.dto.task.TaskResponseDTO;
import com.ivan.taskflowapi.handler.RestErrorMessage;
import com.ivan.taskflowapi.models.enums.TaskStatus;
import com.ivan.taskflowapi.service.TaskService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
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

import java.util.List;

@Tag(name = "Task")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "List project tasks", description = "Returns a paginated list of tasks for a project.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "User is not a project member",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public Page<TaskResponseDTO> findAll(
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId,
            @ParameterObject Pageable pageable) {
        return taskService.findAll(projectId, pageable);
    }

    @PostMapping
    @Operation(summary = "Create task", description = "Creates a task in a project owned by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Request body is invalid",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "User cannot create tasks in this project",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project or membership not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<TaskResponseDTO> create(
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId,
            @RequestBody @Valid TaskRequestDTO request) {
        TaskResponseDTO task = taskService.create(request, projectId);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @PatchMapping("/{taskId}/complete")
    @Operation(summary = "Complete task", description = "Marks an assigned task as completed.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task completed successfully"),
            @ApiResponse(responseCode = "400", description = "Task has no assignee",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "User cannot complete this task",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project, task, or membership not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<Void> completeTask(
            @Parameter(description = "Task identifier", example = "2") @PathVariable Long taskId,
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId) {
        taskService.completeTask(projectId, taskId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/claim")
    @Operation(summary = "Claim task", description = "Assigns an unclaimed task to the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task claimed successfully"),
            @ApiResponse(responseCode = "400", description = "Task cannot be claimed",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Task does not belong to the project",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project or task not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<Void> claim(
            @Parameter(description = "Task identifier", example = "2") @PathVariable Long taskId,
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId) {
        taskService.claim(projectId, taskId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/release")
    @Operation(summary = "Release task", description = "Unassigns a task claimed by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task released successfully"),
            @ApiResponse(responseCode = "400", description = "Task has no assignee",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "User is not the task claimer",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project or task not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<Void> release(
            @Parameter(description = "Task identifier", example = "2") @PathVariable Long taskId,
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId) {
        taskService.release(projectId, taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter tasks by status", description = "Returns a paginated list of project tasks matching the requested status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Task status is invalid"),
            @ApiResponse(responseCode = "403", description = "User is not a project member",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project or membership not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public Page<TaskResponseDTO> groupByStatus(
            @Parameter(description = "Task status", example = "IN_PROGRESS")
            @RequestParam(value = "status") TaskStatus status,
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId,
            @ParameterObject Pageable pageable) {
        return taskService.findByStatus(status, projectId, pageable);
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update task", description = "Updates the title and description of a task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Request body is invalid",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "User cannot update this task",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project, task, or membership not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<TaskResponseDTO> update(
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId,
            @Parameter(description = "Task identifier", example = "2") @PathVariable Long taskId,
            @RequestBody @Valid TaskRequestDTO request) {
        TaskResponseDTO task = taskService.update(projectId, taskId, request);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete task", description = "Deletes a task from a project owned or administered by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User cannot delete this task",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project, task, or membership not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Project identifier", example = "1") @PathVariable Long projectId,
            @Parameter(description = "Task identifier", example = "2") @PathVariable Long taskId) {
        taskService.delete(taskId, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
