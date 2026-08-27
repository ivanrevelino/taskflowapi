package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.task_comment.TaskCommentRequestDTO;
import com.ivan.taskflowapi.dto.task_comment.TaskCommentResponseDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.mapper.TaskCommentMapper;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.ProjectMember;
import com.ivan.taskflowapi.models.Task;
import com.ivan.taskflowapi.models.TaskComment;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.ProjectMemberRole;
import com.ivan.taskflowapi.repository.ProjectMemberRepository;
import com.ivan.taskflowapi.repository.ProjectRepository;
import com.ivan.taskflowapi.repository.TaskCommentRepository;
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
public class TaskCommentService {

    private final TaskCommentRepository repository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskCommentMapper taskCommentMapper;
    private final UserService userService;

    @Transactional
    public TaskCommentResponseDTO create(Long projectId, Long taskId, @Valid TaskCommentRequestDTO request) {
        User user = userService.getAuthenticatedUser();
        Task task = getTaskInProject(projectId, taskId);

        getProjectMember(projectId, user);

        TaskComment commentToBeSaved = TaskComment.builder()
                .content(request.content())
                .task(task)
                .user(user)
                .build();

        TaskComment saved = repository.save(commentToBeSaved);

        log.info("CREATION SUCCESS - User(id: {}, username: {}) created TaskComment(id: {}) for Task(id: {})",
                user.getId(), user.getUsername(), saved.getId(), task.getId());
        return taskCommentMapper.toDTO(saved);
    }

    public List<TaskCommentResponseDTO> findAll(Long projectId, Long taskId) {
        User user = userService.getAuthenticatedUser();
        getTaskInProject(projectId, taskId);

        getProjectMember(projectId, user);

        return repository.findByTaskIdOrderByCreatedAtAsc(taskId).stream().map(taskCommentMapper::toDTO).toList();
    }

    @Transactional
    public TaskCommentResponseDTO update(Long projectId, Long taskId, Long commentId, @Valid TaskCommentRequestDTO request) {
        User user = userService.getAuthenticatedUser();
        Task task = getTaskInProject(projectId, taskId);
        TaskComment comment = getCommentInTask(commentId, task.getId());

        getProjectMember(projectId, user);
        validateCommentAuthor(comment, user);

        comment.setContent(request.content());
        TaskComment saved = repository.save(comment);

        log.info("UPDATE SUCCESS - User(id: {}, username: {}) updated TaskComment(id: {}) for Task(id: {})",
                user.getId(), user.getUsername(), saved.getId(), task.getId());
        return taskCommentMapper.toDTO(saved);
    }

    @Transactional
    public void delete(Long projectId, Long taskId, Long commentId) {
        User user = userService.getAuthenticatedUser();
        Task task = getTaskInProject(projectId, taskId);
        TaskComment comment = getCommentInTask(commentId, task.getId());
        ProjectMember member = getProjectMember(projectId, user);

        if (!isCommentAuthor(comment, user)
                && member.getRole() != ProjectMemberRole.ADMIN
                && member.getRole() != ProjectMemberRole.OWNER) {
            throw new ForbiddenException("You're not authorized to delete this comment");
        }

        repository.delete(comment);

        log.info("DELETE SUCCESS - User(id: {}, username: {}) deleted TaskComment(id: {}) for Task(id: {})",
                user.getId(), user.getUsername(), comment.getId(), task.getId());
    }

    private Task getTaskInProject(Long projectId, Long taskId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getProject().getId().equals(project.getId())) {
            throw new BadRequestException("Task does not belong to this project");
        }

        return task;
    }

    private TaskComment getCommentInTask(Long commentId, Long taskId) {
        return repository.findByIdAndTaskId(commentId, taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }

    private ProjectMember getProjectMember(Long projectId, User user) {
        return projectMemberRepository.findByProjectIdAndUser(projectId, user)
                .orElseThrow(() -> new ForbiddenException("You're not a member of this project"));
    }

    private void validateCommentAuthor(TaskComment comment, User user) {
        if (!isCommentAuthor(comment, user)) {
            throw new ForbiddenException("You're not authorized to update this comment");
        }
    }

    private boolean isCommentAuthor(TaskComment comment, User user) {
        return comment.getUser().getId().equals(user.getId());
    }
}
