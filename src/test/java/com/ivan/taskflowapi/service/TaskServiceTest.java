package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.mapper.TaskMapper;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.Task;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.TaskStatus;
import com.ivan.taskflowapi.models.enums.UserRoles;
import com.ivan.taskflowapi.repository.ProjectMemberRepository;
import com.ivan.taskflowapi.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void claimShouldAssignTaskAndSetStatusToInProgress() {

        User user = User.builder()
                .id(1L)
                .name("Vasquinho")
                .username("srvasquinho")
                .password("123456")
                .role(UserRoles.USER)
                .createdAt(LocalDateTime.now())
                .build();

        Project project = Project.builder()
                .name("Enterprise Management")
                .description("########")
                .id(1L)
                .owner(user)
                .createdAt(LocalDateTime.now())
                .build();

        Task task = Task.builder()
                .title("Fazer arroz")
                .description("Fazer arroz com cenoura")
                .id(1L)
                .createdAt(LocalDateTime.now())
                .assignee(null)
                .comments(null)
                .status(TaskStatus.TO_DO)
                .build();

        task.setProject(project);
        project.setTasks(List.of(task));

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(projectService.findById(1L)).thenReturn(project);
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        taskService.claim(1L, 1L);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(task.getAssignee()).isEqualTo(user);
    }

    @Test
    void claimShouldThrowBadRequestExceptionWhenStatusIsNotTO_DO() {

        User user = User.builder()
                .id(1L)
                .name("Vasquinho")
                .username("srvasquinho")
                .password("123456")
                .role(UserRoles.USER)
                .createdAt(LocalDateTime.now())
                .build();

        Project project = Project.builder()
                .name("Enterprise Management")
                .description("########")
                .id(1L)
                .owner(user)
                .createdAt(LocalDateTime.now())
                .build();

        Task task = Task.builder()
                .title("Fazer arroz")
                .description("Fazer arroz com cenoura")
                .id(1L)
                .createdAt(LocalDateTime.now())
                .assignee(null)
                .comments(null)
                .status(TaskStatus.IN_PROGRESS)
                .build();

        task.setProject(project);
        project.setTasks(List.of(task));

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(projectService.findById(1L)).thenReturn(project);
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.claim(1L, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Only tasks with TO_DO status can be claimed");
    }

    @Test
    void claimShouldThrowBadRequestExceptionWhenTaskIsCurrentlyClaimed() {

        User user = User.builder()
                .id(1L)
                .name("Vasquinho")
                .username("srvasquinho")
                .password("123456")
                .role(UserRoles.USER)
                .createdAt(LocalDateTime.now())
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("Adalberto")
                .username("sradalberto")
                .password("123456")
                .role(UserRoles.USER)
                .createdAt(LocalDateTime.now())
                .build();

        Project project = Project.builder()
                .name("Enterprise Management")
                .description("########")
                .id(1L)
                .owner(user)
                .createdAt(LocalDateTime.now())
                .build();

        Task task = Task.builder()
                .title("Fazer arroz")
                .description("Fazer arroz com cenoura")
                .id(1L)
                .createdAt(LocalDateTime.now())
                .assignee(user2)
                .comments(null)
                .status(TaskStatus.IN_PROGRESS)
                .build();

        task.setProject(project);
        project.setTasks(List.of(task));

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(projectService.findById(1L)).thenReturn(project);
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.claim(1L, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("This task is being assigned");
    }

    @Test
    void claimShouldThrowForbiddenExceptionWhenTheTaskDoesNotBelongToProject() {

        User user = User.builder()
                .id(1L)
                .name("Vasquinho")
                .username("srvasquinho")
                .password("123456")
                .role(UserRoles.USER)
                .createdAt(LocalDateTime.now())
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("Adalberto")
                .username("sradalberto")
                .password("123456")
                .role(UserRoles.USER)
                .createdAt(LocalDateTime.now())
                .build();

        Project project = Project.builder()
                .name("Enterprise Management")
                .description("########")
                .id(1L)
                .owner(user)
                .createdAt(LocalDateTime.now())
                .build();

        Project project2 = Project.builder()
                .name("Hamburguer Cooking")
                .description("Bakering Hamburguer")
                .id(2L)
                .owner(user)
                .createdAt(LocalDateTime.now())
                .build();

        Task task = Task.builder()
                .title("Fazer arroz")
                .description("Fazer arroz com cenoura")
                .id(1L)
                .createdAt(LocalDateTime.now())
                .assignee(user2)
                .comments(null)
                .status(TaskStatus.IN_PROGRESS)
                .build();

        task.setProject(project2);
        project2.setTasks(List.of(task));

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(projectService.findById(1L)).thenReturn(project);
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.claim(1L, 1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Task does not belong to this project");
    }
}