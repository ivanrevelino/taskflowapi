package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.task.TaskRequestDTO;
import com.ivan.taskflowapi.dto.task.TaskResponseDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.mapper.TaskMapper;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.Task;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.TaskStatus;
import com.ivan.taskflowapi.repository.TaskRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository repository;
    private final ProjectService projectService;
    private final UserService userService;
    private final TaskMapper taskMapper;

    @Transactional
    public Task create(@Valid TaskRequestDTO request, Long projectId) {

        User owner = userService.getAuthenticatedUser();
        Project project = projectService.findMyProjectById(projectId);

        validateProjectOwnership(project, owner);

        Task taskToBeSaved = Task.builder().title(request.title()).description(request.description()).project(project).build();
        taskToBeSaved.setStatus(TaskStatus.TO_DO);
        Task saved = repository.save(taskToBeSaved);

        log.info("CREATION SUCCESS - User(id: {}, username: {}) created Task(id: {}, title: {}) for Project(id: {}, name: {})",
                owner.getId(), owner.getUsername(), saved.getId(), saved.getTitle(), project.getId(), project.getName());
        return saved;
    }

    public List<TaskResponseDTO> findMyTasks(Long projectId) {

        User owner = userService.getAuthenticatedUser();
        Project project = projectService.findMyProjectById(projectId);

        validateProjectOwnership(project, owner);

        return repository.findByProjectId(projectId).stream().map(taskMapper::toDTO).toList();
    }

    public Task findByIdOrThrowResourceNotFoundException(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task Not Found"));
    }

    @Transactional
    public TaskResponseDTO completeTask(Long projectId, Long taskId) {
        User owner = userService.getAuthenticatedUser();
        Project project = projectService.findMyProjectById(projectId);

        validateProjectOwnership(project, owner);

        Task task = findByIdOrThrowResourceNotFoundException(taskId);

        if (!task.getProject().getId().equals(project.getId())) {
            throw new ForbiddenException("You cannot complete this task");
        }

        task.setStatus(TaskStatus.COMPLETED);
        Task saved = repository.save(task);
        return new TaskResponseDTO(saved.getId(), saved.getTitle(), saved.getDescription(), saved.getStatus());
    }

    public List<Task> groupByStatus(TaskStatus status, Long projectId) {

        Project project = projectService.findMyProjectById(projectId);
        User owner = userService.getAuthenticatedUser();

        validateProjectOwnership(project, owner);

        return repository.findByProjectIdAndStatus(projectId, status);
    }

    public void delete(Long taskId, Long projectId) {

        User owner = userService.getAuthenticatedUser();
        Project project = projectService.findMyProjectById(projectId);

        validateProjectOwnership(project, owner);

        Task taskToBeDeleted = findByIdOrThrowResourceNotFoundException(taskId);

        if (!taskToBeDeleted.getProject().getId().equals(project.getId())) {
            throw new BadRequestException("Task does not belong to this project");
        }

        repository.delete(taskToBeDeleted);
    }

    private static void validateProjectOwnership(Project project, User owner) {
        if (!project.getOwner().getId().equals(owner.getId())) {
            throw new ForbiddenException("\"You don't own this project\"");
        }
    }
}
