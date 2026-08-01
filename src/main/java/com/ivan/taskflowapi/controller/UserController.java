package com.ivan.taskflowapi.controller;

import com.ivan.taskflowapi.dto.user.UpdatePasswordDTO;
import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponseDTO> getAuthenticatedUserDTO() {
        return ResponseEntity.ok(userService.getAuthenticatedUserDTO());
    }

    @PutMapping
    public ResponseEntity<Void> updatePassword(@RequestBody UpdatePasswordDTO request) {
        userService.updatePassword(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
