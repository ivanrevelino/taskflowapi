package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.auth.AuthLoginDTO;
import com.ivan.taskflowapi.dto.auth.AuthRegisterDTO;
import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.UserRoles;
import com.ivan.taskflowapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;

    public String login(AuthLoginDTO dto) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
        Authentication authenticated = authenticationManager.authenticate(usernamePassword);
        return " ";
    }

    public UserResponseDTO register(AuthRegisterDTO dto) {

        if (userRepository.findByUsername(dto.username()) != null) throw new RuntimeException("User already exists");

        String encodedPassword = passwordEncoder.encode(dto.password());

        User user = User.builder()
                .name(dto.name())
                .username(dto.username())
                .password(encodedPassword)
                .role(UserRoles.ADMIN).build();
        User saved = userRepository.save(user);
        return new UserResponseDTO(saved.getName(), saved.getUsername(), saved.getRole());
    }
}
