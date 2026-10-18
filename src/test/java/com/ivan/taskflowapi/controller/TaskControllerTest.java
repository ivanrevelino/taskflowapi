package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.task.TaskRequestDTO;
import com.ivan.taskflowapi.dto.task.TaskResponseDTO;
import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.models.enums.TaskStatus;
import com.ivan.taskflowapi.security.SecurityFilter;
import com.ivan.taskflowapi.security.jwt.JWTTokenProvider;
import com.ivan.taskflowapi.service.TaskService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private JWTTokenProvider tokenProvider;

    @MockitoBean
    private SecurityFilter securityFilter;

    @Test
    void createShouldReturnCreatedTask() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO("Task", "Description");
        Mockito.when(taskService.create(request, 1L)).thenReturn(task(2L, "Task", "Description"));

        mockMvc.perform(post("/projects/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.title").value("Task"))
                .andExpect(jsonPath("$.status").value("TO_DO"));
    }

    @Test
    void createShouldRejectBlankTitle() throws Exception {
        mockMvc.perform(post("/projects/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":" ","description":"Description"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void completeTaskShouldReturnNoContent() throws Exception {
        mockMvc.perform(patch("/projects/1/tasks/2/complete"))
                .andExpect(status().isNoContent());

        Mockito.verify(taskService).completeTask(1L, 2L);
    }

    @Test
    void claimTaskShouldReturnNoContent() throws Exception {
        mockMvc.perform(patch("/projects/1/tasks/2/claim"))
                .andExpect(status().isNoContent());

        Mockito.verify(taskService).claim(1L, 2L);
    }

    @Test
    void claimTaskShouldReturnBadRequestWhenTaskCannotBeClaimed() throws Exception {
        Mockito.doThrow(new BadRequestException("This task is being assigned"))
                .when(taskService).claim(1L, 2L);

        mockMvc.perform(patch("/projects/1/tasks/2/claim"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void releaseTaskShouldReturnNoContent() throws Exception {
        mockMvc.perform(patch("/projects/1/tasks/2/release"))
                .andExpect(status().isNoContent());

        Mockito.verify(taskService).release(1L, 2L);
    }

    @Test
    void releaseTaskShouldReturnForbiddenWhenRequesterIsNotClaimer() throws Exception {
        Mockito.doThrow(new ForbiddenException("Not task claimer"))
                .when(taskService).release(1L, 2L);

        mockMvc.perform(patch("/projects/1/tasks/2/release"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateShouldReturnUpdatedTask() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO("Updated", "Updated description");
        Mockito.when(taskService.update(1L, 2L, request))
                .thenReturn(task(2L, "Updated", "Updated description"));

        mockMvc.perform(put("/projects/1/tasks/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void updateShouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO("Updated", "Updated description");
        Mockito.when(taskService.update(1L, 2L, request))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        mockMvc.perform(put("/projects/1/tasks/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/projects/1/tasks/2"))
                .andExpect(status().isNoContent());

        Mockito.verify(taskService).delete(2L, 1L);
    }

    @Test
    void deleteShouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Task not found"))
                .when(taskService).delete(2L, 1L);

        mockMvc.perform(delete("/projects/1/tasks/2"))
                .andExpect(status().isNotFound());
    }

    private static TaskResponseDTO task(Long id, String title, String description) {
        return new TaskResponseDTO(id, title, description, TaskStatus.TO_DO, List.of(), null, null);
    }
}
