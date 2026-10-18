package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.user.UpdatePasswordDTO;
import com.ivan.taskflowapi.dto.user.UpdateUsernameDTO;
import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.models.enums.UserRoles;
import com.ivan.taskflowapi.security.SecurityFilter;
import com.ivan.taskflowapi.security.jwt.JWTTokenProvider;
import com.ivan.taskflowapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JWTTokenProvider tokenProvider;

    @MockitoBean
    private SecurityFilter securityFilter;

    @Test
    void getCurrentUserShouldReturnProfile() throws Exception {
        UserResponseDTO response = UserResponseDTO.builder()
                .id(1L)
                .name("Member")
                .username("member01")
                .createdAt(LocalDateTime.parse("2025-01-01T10:00:00"))
                .role(UserRoles.USER)
                .build();
        Mockito.when(userService.getAuthenticatedUserDTO()).thenReturn(response);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("member01"));
    }

    @Test
    void updatePasswordShouldReturnNoContent() throws Exception {
        UpdatePasswordDTO request = new UpdatePasswordDTO("old-password", "new-password");

        mockMvc.perform(patch("/users/me/update/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).updatePassword(request);
    }

    @Test
    void updatePasswordShouldRejectMissingBody() throws Exception {
        mockMvc.perform(patch("/users/me/update/password")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUsernameShouldReturnNoContent() throws Exception {
        UpdateUsernameDTO request = new UpdateUsernameDTO("new-name");

        mockMvc.perform(patch("/users/me/update/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).updateUsername(request);
    }

    @Test
    void updateUsernameShouldReturnBadRequestWhenAlreadyInUse() throws Exception {
        UpdateUsernameDTO request = new UpdateUsernameDTO("existing");
        Mockito.doThrow(new BadRequestException("Username is already in use"))
                .when(userService).updateUsername(request);

        mockMvc.perform(patch("/users/me/update/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
