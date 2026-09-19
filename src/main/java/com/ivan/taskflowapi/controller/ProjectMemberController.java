package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.project_member.AddMemberRequestDTO;
import com.ivan.taskflowapi.dto.project_member.ProjectMemberResponseDTO;
import com.ivan.taskflowapi.models.ProjectMember;
import com.ivan.taskflowapi.service.ProjectMemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @PostMapping
    @Operation(summary = "Add member", description = "Owner and Admin can add members to project")
    public ResponseEntity<ProjectMemberResponseDTO> addMember(@PathVariable Long projectId, @RequestBody AddMemberRequestDTO request) {
        ProjectMemberResponseDTO projectMember = projectMemberService.addMember(projectId, request);
        return new ResponseEntity<>(projectMember, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Find all members in a project", description = "Returns a list of members of the project")
    public List<ProjectMemberResponseDTO> findAllMembers(@PathVariable Long projectId) {
        return projectMemberService.findAll(projectId);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> delete(@PathVariable Long projectId, @PathVariable Long memberId) {
        projectMemberService.delete(projectId, memberId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/leave")
    public ResponseEntity<Void> leaveProject(@PathVariable Long projectId) {
        projectMemberService.leaveProject(projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
