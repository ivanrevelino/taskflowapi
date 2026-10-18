package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.task_comment.TaskCommentRequestDTO;
import com.ivan.taskflowapi.dto.task_comment.TaskCommentResponseDTO;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.security.SecurityFilter;
import com.ivan.taskflowapi.security.jwt.JWTTokenProvider;
import com.ivan.taskflowapi.service.TaskCommentService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskCommentController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskCommentService taskCommentService;

    @MockitoBean
    private JWTTokenProvider tokenProvider;

    @MockitoBean
    private SecurityFilter securityFilter;

    @Test
    void createShouldReturnCreatedComment() throws Exception {
        TaskCommentRequestDTO request = new TaskCommentRequestDTO("Comment");
        Mockito.when(taskCommentService.create(1L, 2L, request))
                .thenReturn(new TaskCommentResponseDTO(3L, "Comment", null, 2L, null, null));

        mockMvc.perform(post("/projects/1/tasks/2/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.content").value("Comment"));
    }

    @Test
    void createShouldRejectBlankComment() throws Exception {
        mockMvc.perform(post("/projects/1/tasks/2/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":" "}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void replyShouldReturnCreatedReply() throws Exception {
        TaskCommentRequestDTO request = new TaskCommentRequestDTO("Reply");
        Mockito.when(taskCommentService.reply(1L, 2L, 3L, request))
                .thenReturn(new TaskCommentResponseDTO(4L, "Reply", null, 2L, 3L, null));

        mockMvc.perform(post("/projects/1/tasks/2/comments/3/reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4L))
                .andExpect(jsonPath("$.parentCommentId").value(3L));
    }

    @Test
    void replyShouldReturnNotFoundWhenParentCommentDoesNotExist() throws Exception {
        TaskCommentRequestDTO request = new TaskCommentRequestDTO("Reply");
        Mockito.when(taskCommentService.reply(1L, 2L, 3L, request))
                .thenThrow(new ResourceNotFoundException("Comment not found"));

        mockMvc.perform(post("/projects/1/tasks/2/comments/3/reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllShouldReturnComments() throws Exception {
        Mockito.when(taskCommentService.findAll(1L, 2L))
                .thenReturn(List.of(new TaskCommentResponseDTO(3L, "Comment", null, 2L, null, null)));

        mockMvc.perform(get("/projects/1/tasks/2/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].content").value("Comment"));
    }

    @Test
    void updateShouldReturnUpdatedComment() throws Exception {
        TaskCommentRequestDTO request = new TaskCommentRequestDTO("Updated");
        Mockito.when(taskCommentService.update(1L, 2L, 3L, request))
                .thenReturn(new TaskCommentResponseDTO(3L, "Updated", null, 2L, null, null));

        mockMvc.perform(put("/projects/1/tasks/2/comments/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated"));
    }

    @Test
    void updateShouldReturnForbiddenWhenRequesterIsNotAuthor() throws Exception {
        TaskCommentRequestDTO request = new TaskCommentRequestDTO("Updated");
        Mockito.when(taskCommentService.update(1L, 2L, 3L, request))
                .thenThrow(new ForbiddenException("You're not authorized to update this comment"));

        mockMvc.perform(put("/projects/1/tasks/2/comments/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/projects/1/tasks/2/comments/3"))
                .andExpect(status().isNoContent());

        Mockito.verify(taskCommentService).delete(1L, 2L, 3L);
    }

    @Test
    void deleteShouldReturnForbiddenWhenRequesterCannotDeleteComment() throws Exception {
        Mockito.doThrow(new ForbiddenException("Not authorized"))
                .when(taskCommentService).delete(1L, 2L, 3L);

        mockMvc.perform(delete("/projects/1/tasks/2/comments/3"))
                .andExpect(status().isForbidden());
    }
}
