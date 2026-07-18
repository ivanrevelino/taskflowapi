package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.task.TaskRequestDTO;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.mapper.TaskMapper;
import com.ivan.taskflowapi.models.Task;
import com.ivan.taskflowapi.repository.TaskRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;
    private final TaskMapper taskMapper;

    @Transactional
    public Task create(@Valid  TaskRequestDTO request) {
        Task entity = taskMapper.toEntity(request);
        return repository.save(entity);
    }

    public Task findByIdOrThrowResourceNotFoundException(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task Not Found"));
    }

    public void delete(Long id) {
        Task taskToBeDeleted = findByIdOrThrowResourceNotFoundException(id);
        repository.delete(taskToBeDeleted);
    }
}
