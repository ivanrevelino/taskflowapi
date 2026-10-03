package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.project.ProjectRequestDTO;
import com.ivan.taskflowapi.dto.project.ProjectResponseDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.mapper.ProjectMapper;
import com.ivan.taskflowapi.mapper.UserMapper;
import com.ivan.taskflowapi.mapper.manual_mapper.ProjectMapperMnl;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.UserRoles;
import com.ivan.taskflowapi.repository.ProjectMemberRepository;
import com.ivan.taskflowapi.repository.ProjectRepository;
import com.ivan.taskflowapi.repository.ProjectRepositoryTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

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
    @DisplayName("Should return project when user is a project member")
    void findByIdShouldReturnProjectWhenUserIsProjectMember() {

        User user = User.builder()
                .id(1L)
                .role(UserRoles.ADMIN)
                .name("Ivan")
                .username("srmbilane")
                .password("1224")
                .createdAt(LocalDateTime.now())
                .build();

        Project project = Project.builder()
                .id(1L)
                .name("Enterprise")
                .description("Create a enterprise business")
                .owner(user)
                .createdAt(LocalDateTime.now())
                .build();

        Optional<Project> projectOptional = Optional.of(project);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(repository.findById(1L)).thenReturn(projectOptional);
        when(projectMemberRepository.existsByProjectIdAndUserId(1L, 1L))
                .thenReturn(true);

        Project response = projectService.findById(1L);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw ForbiddenException when user is not a project member")
    void findByIdShouldNotReturnProjectWhenUserIsNotProjectMember() {

        User user = User.builder()
                .id(1L)
                .role(UserRoles.ADMIN)
                .name("Ivan")
                .username("srmbilane")
                .password("1224")
                .createdAt(LocalDateTime.now())
                .build();

        Project project = Project.builder()
                .id(1L)
                .name("Enterprise")
                .description("Create a enterprise business")
                .owner(user)
                .createdAt(LocalDateTime.now())
                .build();

        Optional<Project> projectOptional = Optional.of(project);

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(repository.findById(1L)).thenReturn(projectOptional);
        when(projectMemberRepository.existsByProjectIdAndUserId(1L, 1L))
                .thenReturn(false);

        Assertions.assertThatThrownBy(() -> projectService.findById(1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You don't make part of this project");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when project does not exists")
    void findByIdShouldThrowResourceNotFoundExceptionWhenProjectDoesNotExists() {

        User user = User.builder()
                .id(1L)
                .role(UserRoles.ADMIN)
                .name("Ivan")
                .username("srmbilane")
                .password("1224")
                .createdAt(LocalDateTime.now())
                .build();

        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> projectService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Project not found");
    }


    @Test
    void delete() {
    }

    @Test
    void update() {
    }
}
