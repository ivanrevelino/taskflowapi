package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.project_member.AddMemberRequestDTO;
import com.ivan.taskflowapi.dto.project_member.ProjectMemberResponseDTO;
import com.ivan.taskflowapi.models.ProjectMember;
import com.ivan.taskflowapi.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @PostMapping
    public ResponseEntity<ProjectMemberResponseDTO> addMember(@PathVariable Long projectId, @RequestBody AddMemberRequestDTO request) {
        ProjectMemberResponseDTO projectMember = projectMemberService.addMember(projectId, request);
        return new ResponseEntity<>(projectMember, HttpStatus.CREATED);
    }

    @GetMapping
    public Page<ProjectMemberResponseDTO> findAllMembers(Pageable pageable) {
        return projectMemberService.listAll(pageable);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> delete(@PathVariable Long projectId, @PathVariable Long memberId) {
        projectMemberService.delete(projectId, memberId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
