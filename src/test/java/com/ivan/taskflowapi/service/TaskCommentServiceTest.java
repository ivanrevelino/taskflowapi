package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.task_comment.TaskCommentRequestDTO;
import com.ivan.taskflowapi.dto.task_comment.TaskCommentResponseDTO;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.mapper.TaskCommentMapper;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.ProjectMember;
import com.ivan.taskflowapi.models.Task;
import com.ivan.taskflowapi.models.TaskComment;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.ProjectMemberRole;
import com.ivan.taskflowapi.models.enums.TaskStatus;
import com.ivan.taskflowapi.models.enums.UserRoles;
import com.ivan.taskflowapi.repository.ProjectMemberRepository;
import com.ivan.taskflowapi.repository.ProjectRepository;
import com.ivan.taskflowapi.repository.TaskCommentRepository;
import com.ivan.taskflowapi.repository.TaskRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskCommentServiceTest {

    @Mock
    private TaskCommentRepository repository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private TaskCommentMapper taskCommentMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskCommentService taskCommentService;

    @Test
    void create_ShouldSaveComment_WhenUserIsProjectMember() {
        User user = user(1L);
        Project project = project(1L);
        Task task = task(1L, project);
        ProjectMember member = member(project, user, ProjectMemberRole.MEMBER);
        TaskCommentRequestDTO request = new TaskCommentRequestDTO("First comment");
        TaskComment saved = TaskComment.builder().id(1L).content(request.content()).task(task).user(user).build();
        TaskCommentResponseDTO response = new TaskCommentResponseDTO(1L, request.content(), null, task.getId(), LocalDateTime.now());

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(projectMemberRepository.findByProjectIdAndUser(project.getId(), user)).thenReturn(Optional.of(member));
        when(repository.save(any(TaskComment.class))).thenReturn(saved);
        when(taskCommentMapper.toDTO(saved)).thenReturn(response);

        TaskCommentResponseDTO result = taskCommentService.create(project.getId(), task.getId(), request);

        ArgumentCaptor<TaskComment> captor = ArgumentCaptor.forClass(TaskComment.class);
        verify(repository).save(captor.capture());
        Assertions.assertThat(result).isEqualTo(response);
        Assertions.assertThat(captor.getValue().getContent()).isEqualTo(request.content());
        Assertions.assertThat(captor.getValue().getTask()).isEqualTo(task);
        Assertions.assertThat(captor.getValue().getUser()).isEqualTo(user);
    }

    @Test
    void update_ShouldThrowForbiddenException_WhenUserIsNotCommentAuthor() {
        User user = user(1L);
        User author = user(2L);
        Project project = project(1L);
        Task task = task(1L, project);
        TaskComment comment = TaskComment.builder().id(1L).content("Original").task(task).user(author).build();

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(repository.findByIdAndTaskId(comment.getId(), task.getId())).thenReturn(Optional.of(comment));
        when(projectMemberRepository.findByProjectIdAndUser(project.getId(), user))
                .thenReturn(Optional.of(member(project, user, ProjectMemberRole.ADMIN)));

        Assertions.assertThatThrownBy(() -> taskCommentService.update(project.getId(), task.getId(), comment.getId(), new TaskCommentRequestDTO("Updated")))
                .isInstanceOf(ForbiddenException.class);

        verify(repository, never()).save(any(TaskComment.class));
    }

    @Test
    void delete_ShouldDeleteComment_WhenUserIsAdminAndNotAuthor() {
        User admin = user(1L);
        User author = user(2L);
        Project project = project(1L);
        Task task = task(1L, project);
        TaskComment comment = TaskComment.builder().id(1L).content("Comment").task(task).user(author).build();

        when(userService.getAuthenticatedUser()).thenReturn(admin);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(repository.findByIdAndTaskId(comment.getId(), task.getId())).thenReturn(Optional.of(comment));
        when(projectMemberRepository.findByProjectIdAndUser(project.getId(), admin))
                .thenReturn(Optional.of(member(project, admin, ProjectMemberRole.ADMIN)));

        taskCommentService.delete(project.getId(), task.getId(), comment.getId());

        verify(repository).delete(comment);
    }

    @Test
    void delete_ShouldThrowForbiddenException_WhenUserIsMemberAndNotAuthor() {
        User user = user(1L);
        User author = user(2L);
        Project project = project(1L);
        Task task = task(1L, project);
        TaskComment comment = TaskComment.builder().id(1L).content("Comment").task(task).user(author).build();

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(repository.findByIdAndTaskId(comment.getId(), task.getId())).thenReturn(Optional.of(comment));
        when(projectMemberRepository.findByProjectIdAndUser(project.getId(), user))
                .thenReturn(Optional.of(member(project, user, ProjectMemberRole.MEMBER)));

        Assertions.assertThatThrownBy(() -> taskCommentService.delete(project.getId(), task.getId(), comment.getId()))
                .isInstanceOf(ForbiddenException.class);

        verify(repository, never()).delete(any(TaskComment.class));
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setUsername("user" + id);
        user.setPassword("password");
        user.setRole(UserRoles.USER);
        return user;
    }

    private Project project(Long id) {
        Project project = new Project();
        project.setId(id);
        project.setName("Project");
        return project;
    }

    private Task task(Long id, Project project) {
        return Task.builder()
                .id(id)
                .title("Task")
                .description("Task description")
                .status(TaskStatus.TO_DO)
                .project(project)
                .build();
    }

    private ProjectMember member(Project project, User user, ProjectMemberRole role) {
        return ProjectMember.builder()
                .project(project)
                .user(user)
                .role(role)
                .build();
    }
}
