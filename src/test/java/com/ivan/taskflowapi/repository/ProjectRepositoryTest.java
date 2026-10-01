package com.ivan.taskflowapi.repository;

import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.UserRoles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateProjectWhenSuccessful() {
        Project project = generateProject();

        assertThat(project.getId()).isNotNull();
        assertThat(project).isNotNull();
    }

    @Test
    void findByIdShouldReturnRightProjectWhenSuccessful() {
        Project project = generateProject();
        long id = project.getId();

        Optional<Project> projectById = projectRepository.findById(id);

        assertThat(projectById).isNotEmpty();
        assertThat(projectById.get().getId()).isNotNull();
        assertThat(projectById.get().getId()).isEqualTo(id);
        assertThat(projectById.get()).usingRecursiveComparison().isEqualTo(project);
    }

    @Test
    void findByIdShouldNotReturnRightProjectWhenIdDoesNotExists() {
        Project project = generateProject();
        long id = 9000;

        Optional<Project> projectById = projectRepository.findById(id);

        assertThat(projectById).isEmpty();
    }

    @Test
    void deleteShouldRemoveProjectWhenSuccessful() {
        Project project = generateProject();

        projectRepository.delete(project);
        Optional<Project> projectById = projectRepository.findById(project.getId());

        assertThat(projectById).isEmpty();
    }

    private Project generateProject() {

        User user = User.builder().name("Ivan Revelino").username("srmbilane").password("12344").role(UserRoles.ADMIN).build();

        User owner = userRepository.save(user);

        Project project = Project.builder()
                .name("Tech Environment")
                .description("Tech Solutions that helps environment")
                .owner(owner)
                .build();
        return projectRepository.save(project);
    }
}
