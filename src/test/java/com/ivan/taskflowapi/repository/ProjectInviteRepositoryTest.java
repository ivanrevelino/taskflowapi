package com.ivan.taskflowapi.repository;

import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.ProjectInvite;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.InviteStatus;
import com.ivan.taskflowapi.models.enums.UserRoles;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

@DataJpaTest
class ProjectInviteRepositoryTest {

    @Autowired
    private ProjectInviteRepository inviteRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    private Project project;
    private User invited;

    @BeforeEach
    void setUp() {
        User owner = userRepository.save(user("owner"));
        invited = userRepository.save(user("invited"));
        User otherInvited = userRepository.save(user("other"));
        project = projectRepository.save(Project.builder().name("Project").owner(owner).build());

        saveInvite(InviteStatus.PENDING, invited);
        saveInvite(InviteStatus.ACCEPTED, invited);
        saveInvite(InviteStatus.PENDING, otherInvited);
    }

    @Test
    void findByInvitedIdReturnsOnlyInvitesReceivedByUserAndUsesPagination() {
        Page<ProjectInvite> result = inviteRepository.findByInvited_Id(invited.getId(), PageRequest.of(0, 1));

        Assertions.assertThat(result.getContent()).hasSize(1);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(2);
        Assertions.assertThat(result.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(result.getContent().get(0).getInvited().getId()).isEqualTo(invited.getId());
    }

    @Test
    void findByProjectWithStatusFiltersByStatusAndPaginates() {
        Page<ProjectInvite> result = inviteRepository.findByProjectWithStatus(
                project.getId(), InviteStatus.PENDING, PageRequest.of(0, 1));

        Assertions.assertThat(result.getContent()).hasSize(1);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(2);
        Assertions.assertThat(result.getContent())
                .allSatisfy(invite -> Assertions.assertThat(invite.getStatus()).isEqualTo(InviteStatus.PENDING));
    }

    @Test
    void findByProjectWithStatusReturnsAllStatusesWhenFilterIsNull() {
        Page<ProjectInvite> result = inviteRepository.findByProjectWithStatus(
                project.getId(), null, PageRequest.of(0, 10));

        Assertions.assertThat(result.getContent()).hasSize(3);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(3);
    }

    private User user(String username) {
        return User.builder()
                .name(username)
                .username(username)
                .password("password")
                .role(UserRoles.USER)
                .build();
    }

    private void saveInvite(InviteStatus status, User recipient) {
        inviteRepository.save(ProjectInvite.builder()
                .project(project)
                .invited(recipient)
                .inviter(project.getOwner())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .status(status)
                .build());
    }
}
