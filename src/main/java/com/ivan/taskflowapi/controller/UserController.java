package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.user.UpdatePasswordDTO;
import com.ivan.taskflowapi.dto.user.UpdateUsernameDTO;
import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.handler.RestErrorMessage;
import com.ivan.taskflowapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns the profile of the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication is required")
    })
    public ResponseEntity<UserResponseDTO> getAuthenticatedUserDTO() {
        return ResponseEntity.ok(userService.getAuthenticatedUserDTO());
    }

    @PatchMapping("/me/update/password")
    @Operation(summary = "Update password", description = "Changes the authenticated user's password.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Request body is invalid",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "Current password is incorrect")
    })
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid UpdatePasswordDTO request) {
        userService.updatePassword(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("me/update/username")
    @Operation(summary = "Update username", description = "Changes the authenticated user's username.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Username updated successfully"),
            @ApiResponse(responseCode = "400", description = "Username is invalid or already in use",
                    content = @Content(schema = @Schema(implementation = RestErrorMessage.class)))
    })
    public ResponseEntity<Void> updateUsername(@RequestBody @Valid UpdateUsernameDTO request) {
        userService.updateUsername(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Searches users by a case-insensitive username fragment.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Username query parameter is required")
    })
    public Page<UserResponseDTO> searchUsers(
            @Parameter(description = "Username fragment to search for", example = "member")
            @RequestParam String username,
            @ParameterObject Pageable pageable) {
        return userService.searchUsers(username, pageable);
    }
}
