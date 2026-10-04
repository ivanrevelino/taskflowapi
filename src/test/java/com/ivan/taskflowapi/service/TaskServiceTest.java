package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.mapper.TaskMapper;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.Task;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.TaskStatus;
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

import static com.ivan.taskflowapi.util.Generator.generateTestProject;
import static com.ivan.taskflowapi.util.Generator.generateTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

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

        User user = generateTestUser(1L, "Vasquinho", "srvasquinho");

        Project project = generateTestProject(1L, "Enterprise Management", "########", user);

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

        User user = generateTestUser(1L, "Vasquinho", "srvasquinho");

        Project project = generateTestProject(1L, "Enterprise Management", "########", user);

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

        User user = generateTestUser(1L, "Vasquinho", "srvasquinho");
        User user2 = generateTestUser(2L, "Adalberto", "sradalberto");

        Project project = generateTestProject(1L, "Enterprise Management", "########", user);

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

        User user = generateTestUser(1L, "Vasquinho", "srvasquinho");
        User user2 = generateTestUser(2L, "Adalberto", "sradalberto");

        Project project = generateTestProject(1L, "Enterprise Management", "########", user);
        Project project2 = generateTestProject(2L, "Hamburguer Cooking", "Bakering Hamburguer", user);

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

    @Test
    void releaseShouldReleaseTaskWhenSuccessful() {

        User user = generateTestUser(1L, "Vasquinho", "srvasquinho");

        Project project = generateTestProject(1L, "Enterprise Management", "########", user);

        Task task = Task.builder()
                .title("Fazer arroz")
                .description("Fazer arroz com cenoura")
                .id(1L)
                .createdAt(LocalDateTime.now())
                .assignee(user)
                .comments(null)
                .status(TaskStatus.TO_DO)
                .build();

        task.setProject(project);
        project.setTasks(List.of(task));

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(projectService.findById(1L)).thenReturn(project);
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        taskService.release(1L, 1L);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.TO_DO);
        assertThat(task.getAssignee()).isNull();
    }

    @Test
    void releaseShouldThrowBadRequestExceptionWhenAssigneeIsNull() {

        User user = generateTestUser(1L, "Vasquinho", "srvasquinho");

        Project project = generateTestProject(1L, "Enterprise Management", "########", user);

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

        assertThatThrownBy(() -> taskService.release(1L, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("This task has no assignee");
    }

    @Test
    void releaseShouldThrowForbiddenWhenTaskIsAlreadyAssigned() {

        User user = generateTestUser(1L, "Vasquinho", "srvasquinho");
        User assignee = generateTestUser(2L, "Adalberto", "sradalberto");

        Project project = generateTestProject(1L, "Enterprise Management", "########", user);

        Task task = Task.builder()
                .title("Fazer arroz")
                .description("Fazer arroz com cenoura")
                .id(1L)
                .createdAt(LocalDateTime.now())
                .assignee(assignee)
                .comments(null)
                .status(TaskStatus.TO_DO)
                .build();

        task.setProject(project);
        project.setTasks(List.of(task));

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(projectService.findById(1L)).thenReturn(project);
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.release(1L, 1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You cannot release this task. Because you're not the claimer");
    }

    @Test
    void releaseShouldThrowForbiddenWhenTaskDoesNotBelongToProject() {
        User user = generateTestUser(1L, "Vasquinho", "srvasquinho");
        User assignee = generateTestUser(2L, "Adalberto", "sradalberto");

        Project project = generateTestProject(1L, "Enterprise Management", "########", user);
        Project project2 = generateTestProject(2L, "Hamburguer Cooking", "Bakering Hamburguer", user);

        Task task = Task.builder()
                .title("Fazer arroz")
                .description("Fazer arroz com cenoura")
                .id(1L)
                .createdAt(LocalDateTime.now())
                .assignee(assignee)
                .comments(null)
                .status(TaskStatus.TO_DO)
                .build();

        task.setProject(project2);
        project2.setTasks(List.of(task));

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(projectService.findById(1L)).thenReturn(project);
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.release(1L, 1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Task does not belong to this project");
    }
}