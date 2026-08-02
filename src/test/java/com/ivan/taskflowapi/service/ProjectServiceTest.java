package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.project.ProjectRequestDTO;
import com.ivan.taskflowapi.dto.project.ProjectResponseDTO;
import com.ivan.taskflowapi.mapper.ProjectMapper;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.UserRoles;
import com.ivan.taskflowapi.repository.ProjectRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Mock
    private ProjectRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private ProjectMapper projectMapper;

    @Autowired
    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should create Project when Successful")
    void create() {
        User user = new User(1L, "Ivan", "srmbilane", "1224", UserRoles.ADMIN, null, LocalDateTime.now());
        ProjectRequestDTO request = new ProjectRequestDTO("Enterprise", "Create a enterprise business");
        Project project = new Project(1L, request.name(), request.description(), user, null, LocalDateTime.now());


        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(projectMapper.toEntity(request)).thenReturn(project);
        when(repository.save(project)).thenReturn(project);

        ProjectResponseDTO result = projectService.create(request);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(project.getOwner()).isEqualTo(user);

    }

    @Test
    void findById() {
    }

    @Test
    void findByIdResponseDTO() {
    }

    @Test
    void delete() {
    }

    @Test
    void update() {
    }
}