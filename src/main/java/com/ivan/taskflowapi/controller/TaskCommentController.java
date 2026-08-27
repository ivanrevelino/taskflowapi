package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.task_comment.TaskCommentRequestDTO;
import com.ivan.taskflowapi.dto.task_comment.TaskCommentResponseDTO;
import com.ivan.taskflowapi.service.TaskCommentService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/tasks/{taskId}/comments")
public class TaskCommentController {

    private final TaskCommentService taskCommentService;

    @PostMapping
    public ResponseEntity<TaskCommentResponseDTO> create(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestBody @Valid TaskCommentRequestDTO request
    ) {
        TaskCommentResponseDTO comment = taskCommentService.create(projectId, taskId, request);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TaskCommentResponseDTO>> findAll(
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ) {
        List<TaskCommentResponseDTO> comments = taskCommentService.findAll(projectId, taskId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<TaskCommentResponseDTO> update(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            @RequestBody @Valid TaskCommentRequestDTO request
    ) {
        TaskCommentResponseDTO comment = taskCommentService.update(projectId, taskId, commentId, request);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @PathVariable Long commentId
    ) {
        taskCommentService.delete(projectId, taskId, commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
