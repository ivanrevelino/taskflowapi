package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.task.TaskRequestDTO;
import com.ivan.taskflowapi.models.Task;
import com.ivan.taskflowapi.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> create(@PathVariable Long projectId, @RequestBody TaskRequestDTO dto) {
        Task task = taskService.create(dto, projectId);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }
}
