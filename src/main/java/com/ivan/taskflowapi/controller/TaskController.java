package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.task.TaskRequestDTO;
import com.ivan.taskflowapi.dto.task.TaskResponseDTO;
import com.ivan.taskflowapi.models.Task;
import com.ivan.taskflowapi.models.enums.TaskStatus;
import com.ivan.taskflowapi.service.TaskService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<TaskResponseDTO>> findAll(@PathVariable Long projectId) {
        List<TaskResponseDTO> tasks = taskService.findMyTasks(projectId);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Task> create(@PathVariable Long projectId, @RequestBody TaskRequestDTO dto) {
        Task task = taskService.create(dto, projectId);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> completeTask(@PathVariable Long taskId, @PathVariable Long projectId) {
        TaskResponseDTO response = taskService.completeTask(projectId, taskId);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping
    public ResponseEntity<List<Task>> groupByStatus(@RequestParam(value = "status") TaskStatus status, Long projectId) {
        List<Task> tasks = taskService.groupByStatus(status, projectId);
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable Long projectId, @PathVariable Long taskId) {
        taskService.delete(taskId, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
