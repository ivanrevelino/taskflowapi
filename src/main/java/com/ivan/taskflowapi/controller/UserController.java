package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.user.UpdatePasswordDTO;
import com.ivan.taskflowapi.dto.user.UpdateUsernameDTO;
import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getAuthenticatedUserDTO() {
        return ResponseEntity.ok(userService.getAuthenticatedUserDTO());
    }

    @GetMapping("/search")
    public Page<UserResponseDTO> searchUsers(@RequestParam String username, Pageable pageable) {
        return userService.searchUsers(username, pageable);
    }

    @PutMapping("/me/update/password")
    public ResponseEntity<Void> updatePassword(@RequestBody UpdatePasswordDTO request) {
        userService.updatePassword(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("me/update/username")
    public ResponseEntity<Void> updateUsername(@RequestBody UpdateUsernameDTO request) {
        userService.updateUsername(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}