package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.task.TaskRequestDTO;
import com.ivan.taskflowapi.dto.task.TaskResponseDTO;
import com.ivan.taskflowapi.models.Task;
import com.ivan.taskflowapi.models.enums.TaskStatus;
import com.ivan.taskflowapi.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "Find all tasks in the project")
    public Page<TaskResponseDTO> findAll(@PathVariable Long projectId, Pageable pageable) {
        return taskService.findAll(projectId, pageable);
    }

    @PostMapping
    @Operation(summary = "Create new task", description = "Creates a task for a project owned by authenticated user")
    public ResponseEntity<TaskResponseDTO> create(@PathVariable Long projectId, @RequestBody @Valid TaskRequestDTO request) {
        TaskResponseDTO task = taskService.create(request, projectId);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @PatchMapping("/{taskId}")
    @Operation(summary = "Complete a task in the owned project")
    public ResponseEntity<TaskResponseDTO> completeTask(@PathVariable Long taskId, @PathVariable Long projectId) {
        TaskResponseDTO response = taskService.completeTask(projectId, taskId);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/filter")
    @Operation(summary = "Group by status")
    public ResponseEntity<List<Task>> groupByStatus(@RequestParam(value = "status") TaskStatus status, @PathVariable Long projectId) {
        List<Task> tasks = taskService.findByStatus(status, projectId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> update(@PathVariable Long projectId, @PathVariable Long taskId, @RequestBody @Valid TaskRequestDTO request) {
        TaskResponseDTO task = taskService.update(projectId, taskId, request);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete the task in the owned project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "When the Task does not exists in database")
    })
    public ResponseEntity<Void> delete(@PathVariable Long projectId, @PathVariable Long taskId) {
        taskService.delete(taskId, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
