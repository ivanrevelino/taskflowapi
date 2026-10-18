package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.project.ProjectRequestDTO;
import com.ivan.taskflowapi.dto.project.ProjectResponseDTO;
import com.ivan.taskflowapi.dto.project.ProjectSummaryResponse;
import com.ivan.taskflowapi.handler.RestErrorMessage;
import com.ivan.taskflowapi.service.ProjectService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Projects")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/projects")
@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/me")
    @Operation(summary = "List user's projects", description = "Returns projects in which the authenticated user participates, regardless of their role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projects retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication is required")
    })
    public Page<ProjectSummaryResponse> findAll(@ParameterObject Pageable pageable) {
        return projectService.findAllProjects(pageable);
    }

    @GetMapping("/me/owned")
    @Operation(summary = "List user's owned projects", description = "Returns only projects owned by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Owned projects retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication is required")
    })
    public Page<ProjectResponseDTO> findMyProjects(@ParameterObject Pageable pageable) {
        return projectService.findMyProjects(pageable);
    }

    @PostMapping
    @Operation(summary = "Create project", description = "Creates a new project owned by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Project created successfully"),
            @ApiResponse(responseCode = "400", description = "Request fields are invalid",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Authentication is required")
    })
    public ResponseEntity<ProjectResponseDTO> create(@RequestBody @Valid ProjectRequestDTO request) {
        ProjectResponseDTO project = projectService.create(request);
        return new ResponseEntity<>(project, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update project", description = "Updates a project owned by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project updated successfully"),
            @ApiResponse(responseCode = "400", description = "Request body is invalid",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "User is not the project owner",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<ProjectResponseDTO> update(
            @Parameter(description = "Project identifier", example = "1")
            @PathVariable @Positive(message = "Id must be greater than 0") Long id,
            @RequestBody @Valid ProjectRequestDTO request) {

        ProjectResponseDTO project = projectService.update(id, request);
        return ResponseEntity.ok(project);

    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID", description = "Returns a project the authenticated user belongs to.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "User is not a project member",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<ProjectResponseDTO> findById(
            @Parameter(description = "Project identifier", example = "1")
            @PathVariable @Positive(message = "Id must be greater than 0") Long id) {
        ProjectResponseDTO project = projectService.findByIdResponseDTO(id);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project", description = "Deletes a project owned by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Project deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Project identifier is invalid",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "User is not the project owner",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Project identifier", example = "1")
            @PathVariable @Positive(message = "Id must be greater than 0") Long id) {
        projectService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}