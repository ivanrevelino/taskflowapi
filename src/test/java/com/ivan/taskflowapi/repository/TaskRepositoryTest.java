package com.ivan.taskflowapi.repository;

import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.Task;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.TaskStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.ivan.taskflowapi.util.Generator.*;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveShouldPersistTaskWhenSuccessful() {
        User savedUser = userRepository.save(generateUser());

        Project project = generateNonSavedProject();
        project.setOwner(savedUser);
        Project savedProject = projectRepository.save(project);

        Task task = generateTask();
        task.setProject(savedProject);

        Task saved = repository.save(task);

        Assertions.assertThat(saved).isNotNull();
        Assertions.assertThat(saved.getId()).isNotNull();
    }

    @Test
    void findByIdShouldReturnRightTaskWhenSuccessful() {
        User savedUser = userRepository.save(generateUser());
        Project project = generateNonSavedProject();

        project.setOwner(savedUser);
        Project savedProject = projectRepository.save(project);

        Task task = generateTask();
        task.setProject(savedProject);

        Task saved = repository.save(task);

        Optional<Task> optionalTask = repository.findById(saved.getId());

        Assertions.assertThat(optionalTask).isPresent();
        Assertions.assertThat(optionalTask.get()).usingRecursiveComparison()
                .isEqualTo(saved);
    }

    @Test
    void findByIdShouldNotReturnTaskWhenIdDoesNotExists() {
        User savedUser = userRepository.save(generateUser());
        Project project = generateNonSavedProject();

        project.setOwner(savedUser);
        Project savedProject = projectRepository.save(project);

        Task task = generateTask();
        task.setProject(savedProject);

        Task saved = repository.save(task);

        long nonExistId = 90L;

        Optional<Task> optionalTask = repository.findById(nonExistId);

        Assertions.assertThat(optionalTask).isEmpty();
    }

    @Test
    void deleteShouldRemoveTaskWhenSuccessful() {
        User savedUser = userRepository.save(generateUser());
        Project project = generateNonSavedProject();

        project.setOwner(savedUser);
        Project savedProject = projectRepository.save(project);

        Task task = generateTask();
        task.setProject(savedProject);

        Task saved = repository.save(task);

        repository.delete(saved);

        Optional<Task> optionalTask = repository.findById(saved.getId());

        Assertions.assertThat(optionalTask).isEmpty();
    }

    @Test
    void updateShouldUpdateTaskWhenSuccessful() {
        User savedUser = userRepository.save(generateUser());
        Project project = generateNonSavedProject();

        project.setOwner(savedUser);
        Project savedProject = projectRepository.save(project);

        Task task = generateTask();
        task.setProject(savedProject);
        Task saved = repository.save(task);

        String title = "Organizar o quarto";

        saved.setTitle(title);

        Task updatedTask = repository.save(saved);

        Assertions.assertThat(updatedTask.getId()).isNotNull();
        Assertions.assertThat(updatedTask.getId()).isEqualTo(saved.getId());
        Assertions.assertThat(updatedTask.getTitle()).isEqualTo(title);
    }

    public static Project generateNonSavedProject(){
        return Project.builder()
                .name("Product manager")
                .description("Nao sei oq escrever")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Task generateTask() {
        return Task.builder()
                .title("Corrigir bugs")
                .description("Correcao de bugs no controller")
                .status(TaskStatus.TO_DO)
                .build();
    }
}