package com.ivan.taskflowapi.repository;

import com.ivan.taskflowapi.dto.leaderboard.LeaderboardEntryDTO;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.ProjectMember;
import com.ivan.taskflowapi.models.Task;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.ProjectMemberRole;
import com.ivan.taskflowapi.models.enums.TaskStatus;
import com.ivan.taskflowapi.models.enums.UserRoles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LeaderboardRepositoryTest {

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldRankMembersIncludeZeroTasksAndSharePositionForTies() {
        Project project = createProject();
        User first = createMember(project, "first");
        User second = createMember(project, "second");
        createMember(project, "zero");

        createCompletedTask(project, first, LocalDateTime.of(2026, 11, 3, 10, 0));
        createCompletedTask(project, first, LocalDateTime.of(2026, 11, 4, 10, 0));
        createCompletedTask(project, second, LocalDateTime.of(2026, 11, 5, 10, 0));
        createCompletedTask(project, second, LocalDateTime.of(2026, 11, 6, 10, 0));

        LocalDateTime now = LocalDateTime.now();
        List<LeaderboardEntryDTO> result = leaderboardRepository.findProjectLeaderboard(project.getId(), true, now, now);

        assertThat(result)
                .extracting(LeaderboardEntryDTO::completedTasks)
                .containsExactly(2L, 2L, 0L, 0L);
        assertThat(result)
                .extracting(LeaderboardEntryDTO::position)
                .containsExactly(1, 1, 2, 2);
    }

    @Test
    void shouldFilterCompletedTasksByInclusiveStartAndExclusiveEnd() {
        Project project = createProject();
        User member = createMember(project, "weekly");
        LocalDateTime start = LocalDateTime.of(2026, 11, 2, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 11, 9, 0, 0);

        createCompletedTask(project, member, start);
        createCompletedTask(project, member, end.minusNanos(1));
        createCompletedTask(project, member, start.minusNanos(1));
        createCompletedTask(project, member, end);

        List<LeaderboardEntryDTO> result = leaderboardRepository.findProjectLeaderboard(project.getId(), false, start, end);

        assertThat(result).first()
                .extracting(LeaderboardEntryDTO::completedTasks)
                .isEqualTo(2L);
    }

    @Test
    void shouldOnlyCountCompletedTasksAndTasksInTheRequestedProject() {
        Project project = createProject();
        User member = createMember(project, "scoped");
        createCompletedTask(project, member, LocalDateTime.of(2026, 11, 3, 10, 0));

        Task inProgress = task(project, member, TaskStatus.IN_PROGRESS, LocalDateTime.of(2026, 11, 4, 10, 0));
        taskRepository.save(inProgress);

        Project anotherProject = projectRepository.save(Project.builder()
                .name("Other project")
                .owner(member)
                .build());
        Task otherProjectTask = task(anotherProject, member, TaskStatus.COMPLETED, LocalDateTime.of(2026, 11, 4, 10, 0));
        taskRepository.save(otherProjectTask);

        LocalDateTime now = LocalDateTime.now();
        List<LeaderboardEntryDTO> result = leaderboardRepository.findProjectLeaderboard(project.getId(), true, now, now);

        assertThat(result).first()
                .extracting(LeaderboardEntryDTO::completedTasks)
                .isEqualTo(1L);
    }

    @Test
    void shouldFilterMonthlyTasksUsingMonthStartAndNextMonthStart() {
        Project project = createProject();
        User member = createMember(project, "monthly");
        LocalDateTime start = LocalDateTime.of(2026, 11, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 12, 1, 0, 0);

        createCompletedTask(project, member, start);
        createCompletedTask(project, member, end.minusNanos(1));
        createCompletedTask(project, member, start.minusNanos(1));
        createCompletedTask(project, member, end);

        List<LeaderboardEntryDTO> result = leaderboardRepository.findProjectLeaderboard(project.getId(), false, start, end);

        assertThat(result).first()
                .extracting(LeaderboardEntryDTO::completedTasks)
                .isEqualTo(2L);
    }

    private Project createProject() {
        User owner = createUser("owner");
        Project project = projectRepository.save(Project.builder()
                .name("Leaderboard project")
                .owner(owner)
                .build());
        addMembership(project, owner);
        return project;
    }

    private User createMember(Project project, String username) {
        User user = createUser(username);
        addMembership(project, user);
        return user;
    }

    private User createUser(String username) {
        return userRepository.save(User.builder()
                .name(username)
                .username(username)
                .password("password")
                .role(UserRoles.USER)
                .build());
    }

    private void addMembership(Project project, User user) {
        projectMemberRepository.save(ProjectMember.builder()
                .project(project)
                .user(user)
                .role(ProjectMemberRole.MEMBER)
                .build());
    }

    private void createCompletedTask(Project project, User assignee, LocalDateTime completedAt) {
        taskRepository.save(task(project, assignee, TaskStatus.COMPLETED, completedAt));
    }

    private Task task(Project project, User assignee, TaskStatus status, LocalDateTime completedAt) {
        return Task.builder()
                .title("Task")
                .description("Description")
                .project(project)
                .assignee(assignee)
                .status(status)
                .completedAt(completedAt)
                .build();
    }
}
