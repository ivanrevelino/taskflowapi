package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.project.ProjectRequestDTO;
import com.ivan.taskflowapi.dto.project.ProjectResponseDTO;
import com.ivan.taskflowapi.models.Project;
import com.ivan.taskflowapi.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/projects")
@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    @Operation(summary = "Find my projects", description = "Return all project's of logged user")
    @ApiResponse(responseCode = "200")
    public Page<ProjectResponseDTO> findMyProjects(Pageable pageable) {
        return projectService.findMyProjects(pageable);
    }

    @PostMapping
    @Operation(summary = "Create project", description = "Creates a new project for logged user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Project was successfully created"),
            @ApiResponse(responseCode = "400", description = "Fields are not valid")
    })
    public ResponseEntity<ProjectResponseDTO> create(@RequestBody @Valid ProjectRequestDTO request) {
        ProjectResponseDTO project = projectService.create(request);
        return new ResponseEntity<>(project, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Find by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project updated successfully"),
            @ApiResponse(responseCode = "403", description = "User is not the project owner"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ProjectResponseDTO> update(
            @PathVariable @Positive(message = "Id must be greater than 0") Long id,
            @RequestBody ProjectRequestDTO request) {

        ProjectResponseDTO project = projectService.update(id, request);
        return ResponseEntity.ok(project);

    }

    @GetMapping("/{id}")
    @Operation(summary = "Find project by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "User is not a project member"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ProjectResponseDTO> findById(@PathVariable @Positive(message = "Id must be greater than 0") Long id) {
        ProjectResponseDTO project = projectService.findByIdResponseDTO(id);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Project deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "403", description = "User is not the project owner")
    })
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "Id must be greater than 0") Long id) {
        projectService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}