package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.project.ProjectRequestDTO;
import com.ivan.taskflowapi.dto.project.ProjectResponseDTO;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.security.SecurityFilter;
import com.ivan.taskflowapi.security.jwt.JWTTokenProvider;
import com.ivan.taskflowapi.service.ProjectService
        ;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private JWTTokenProvider tokenProvider;

    @MockitoBean
    private SecurityFilter securityFilter;

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void shouldReturnProjectWhenSuccessful() throws Exception {

        Mockito.when(projectService.findByIdResponseDTO(1L))
                .thenReturn(new ProjectResponseDTO(1L, "Projecto XYZ", "IDK"));

        mockMvc.perform(get("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Projecto XYZ"))
                .andExpect(jsonPath("$.description").value("IDK"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void createShouldReturnRightJsonWhenSuccessful() throws Exception {
        ProjectRequestDTO request = new ProjectRequestDTO("Projecto XYZ", "IDK");
        Mockito.when(projectService.create(request))
                .thenReturn(new ProjectResponseDTO(1L, "Projecto XYZ", "IDK"));

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Projecto XYZ"))
                .andExpect(jsonPath("$.description").value("IDK"));
    }

    @Test
    void createShouldRejectBlankFields() throws Exception {
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":" ","description":"Description"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void shouldThrowExceptionWhenProjectDoesNotExists() throws Exception {

        Mockito.when(projectService.findByIdResponseDTO(1L))
                .thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturnUpdatedProject() throws Exception {
        ProjectRequestDTO request = new ProjectRequestDTO("Updated", "Updated description");
        Mockito.when(projectService.update(1L, request))
                .thenReturn(new ProjectResponseDTO(1L, "Updated", "Updated description"));

        mockMvc.perform(put("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void updateShouldReturnForbiddenWhenUserIsNotOwner() throws Exception {
        ProjectRequestDTO request = new ProjectRequestDTO("Updated", "Updated description");
        Mockito.when(projectService.update(Mockito.eq(1L), Mockito.any(ProjectRequestDTO.class)))
                .thenThrow(new ForbiddenException("Not project owner"));

        mockMvc.perform(put("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/projects/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(projectService).delete(1L);
    }

    @Test
    void deleteShouldReturnNotFoundWhenProjectDoesNotExist() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Project not found"))
                .when(projectService).delete(1L);

        mockMvc.perform(delete("/projects/1"))
                .andExpect(status().isNotFound());
    }
}