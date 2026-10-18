package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.auth.AuthLoginDTO;
import com.ivan.taskflowapi.dto.auth.AuthRegisterDTO;
import com.ivan.taskflowapi.dto.auth.AuthResponse;
import com.ivan.taskflowapi.dto.auth.RefreshTokenRequest;
import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.security.SecurityFilter;
import com.ivan.taskflowapi.security.jwt.JWTTokenProvider;
import com.ivan.taskflowapi.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JWTTokenProvider tokenProvider;

    @MockitoBean
    private SecurityFilter securityFilter;

    @Test
    void loginShouldReturnTokens() throws Exception {
        AuthLoginDTO request = new AuthLoginDTO("member01", "password");
        Mockito.when(authService.login(request)).thenReturn(new AuthResponse("access", "refresh"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"));
    }

    @Test
    void loginShouldRejectBlankCredentials() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"","password":""}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerShouldReturnCreatedUser() throws Exception {
        AuthRegisterDTO request = new AuthRegisterDTO("member01", "Member", "password1");
        UserResponseDTO response = UserResponseDTO.builder()
                .id(1L)
                .name("Member")
                .username("member01")
                .build();
        Mockito.when(authService.register(request)).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("member01"));
    }

    @Test
    void registerShouldReturnBadRequestWhenUserAlreadyExists() throws Exception {
        AuthRegisterDTO request = new AuthRegisterDTO("member01", "Member", "password1");
        Mockito.when(authService.register(request))
                .thenThrow(new BadRequestException("User already exists"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshShouldReturnNewTokens() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh");
        Mockito.when(authService.refresh(request)).thenReturn(new AuthResponse("new-access", "valid-refresh"));

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"))
                .andExpect(jsonPath("$.refreshToken").value("valid-refresh"));
    }

    @Test
    void refreshShouldRejectBlankToken() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":""}
                                """))
                .andExpect(status().isBadRequest());
    }
}
