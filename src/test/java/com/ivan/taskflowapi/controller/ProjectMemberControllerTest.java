package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.project_member.AddMemberRequestDTO;
import com.ivan.taskflowapi.dto.project_member.ProjectMemberResponseDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.models.enums.ProjectMemberRole;
import com.ivan.taskflowapi.security.SecurityFilter;
import com.ivan.taskflowapi.security.jwt.JWTTokenProvider;
import com.ivan.taskflowapi.service.ProjectMemberService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProjectMemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectMemberService projectMemberService;

    @MockitoBean
    private JWTTokenProvider tokenProvider;

    @MockitoBean
    private SecurityFilter securityFilter;

    @Test
    void addMemberShouldReturnCreatedMember() throws Exception {
        AddMemberRequestDTO request = new AddMemberRequestDTO(2L);
        ProjectMemberResponseDTO response = new ProjectMemberResponseDTO();
        response.setId(5L);
        response.setRole(ProjectMemberRole.MEMBER);
        Mockito.when(projectMemberService.addMember(1L, request)).thenReturn(response);

        mockMvc.perform(post("/projects/1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @Test
    void addMemberShouldReturnBadRequestWhenUserIsAlreadyMember() throws Exception {
        AddMemberRequestDTO request = new AddMemberRequestDTO(2L);
        Mockito.when(projectMemberService.addMember(1L, request))
                .thenThrow(new BadRequestException("User already is a member"));

        mockMvc.perform(post("/projects/1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllMembersShouldReturnMembers() throws Exception {
        ProjectMemberResponseDTO member = new ProjectMemberResponseDTO();
        member.setId(5L);
        member.setRole(ProjectMemberRole.MEMBER);
        Mockito.when(projectMemberService.findAll(1L)).thenReturn(List.of(member));

        mockMvc.perform(get("/projects/1/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5L))
                .andExpect(jsonPath("$[0].role").value("MEMBER"));
    }

    @Test
    void findAllMembersShouldReturnNotFoundWhenRequesterIsNotMember() throws Exception {
        Mockito.when(projectMemberService.findAll(1L))
                .thenThrow(new ResourceNotFoundException("You are not a member of this project"));

        mockMvc.perform(get("/projects/1/members"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMemberShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/projects/1/members/2"))
                .andExpect(status().isNoContent());

        Mockito.verify(projectMemberService).delete(1L, 2L);
    }

    @Test
    void deleteMemberShouldReturnForbiddenWhenRequesterIsNotOwner() throws Exception {
        Mockito.doThrow(new ForbiddenException("You are not the owner of this project"))
                .when(projectMemberService).delete(1L, 2L);

        mockMvc.perform(delete("/projects/1/members/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    void leaveProjectShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/projects/1/members/leave"))
                .andExpect(status().isNoContent());

        Mockito.verify(projectMemberService).leaveProject(1L);
    }

    @Test
    void leaveProjectShouldReturnBadRequestWhenOwnerTriesToLeave() throws Exception {
        Mockito.doThrow(new BadRequestException("You cannot leave your own project"))
                .when(projectMemberService).leaveProject(1L);

        mockMvc.perform(delete("/projects/1/members/leave"))
                .andExpect(status().isBadRequest());
    }
}
