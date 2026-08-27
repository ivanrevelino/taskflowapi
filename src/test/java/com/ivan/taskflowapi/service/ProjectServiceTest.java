package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.project.ProjectRequestDTO;
import com.ivan.taskflowapi.dto.project.ProjectResponseDTO;
import com.ivan.taskflowapi.mapper.ProjectMapper;
import com.ivan.taskflowapi.mapper.UserMapper;
import com.ivan.taskflowapi.mapper.manual_mapper.ProjectMapperMnl;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.UserRoles;
import com.ivan.taskflowapi.repository.ProjectMemberRepository;
import com.ivan.taskflowapi.repository.ProjectRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Mock
    private ProjectRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectMemberService projectMemberService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private ProjectMapperMnl projectMapperMnl;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create Project when Successful")
    void create() {
        User user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setUsername("srmbilane");
        user.setPassword("1224");
        user.setRole(UserRoles.ADMIN);
        user.setCreatedAt(LocalDateTime.now());

        ProjectRequestDTO request = new ProjectRequestDTO("Enterprise", "Create a enterprise business");
        Project project = new Project();
        project.setId(1L);
        project.setName(request.name());
        project.setDescription(request.description());
        project.setOwner(user);
        project.setCreatedAt(LocalDateTime.now());


        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(projectMapper.toEntity(request)).thenReturn(project);
        when(repository.save(project)).thenReturn(project);
        doNothing().when(projectMemberService).addOwner(project, user);
        when(projectMapper.toDTO(project)).thenReturn(new ProjectResponseDTO());

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
