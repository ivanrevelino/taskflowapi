package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.dto.auth.AuthLoginDTO;
import com.ivan.taskflowapi.dto.auth.AuthRegisterDTO;
import com.ivan.taskflowapi.dto.auth.LoginResponse;
import com.ivan.taskflowapi.dto.user.UserResponseDTO;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.models.enums.UserRoles;
import com.ivan.taskflowapi.repository.UserRepository;
import com.ivan.taskflowapi.security.jwt.JWTTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JWTTokenProvider tokenProvider;

    public LoginResponse login(AuthLoginDTO dto) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.username(), dto.password());
        Authentication authenticated = authenticationManager.authenticate(usernamePassword);

        User user = (User) authenticated.getPrincipal();
        assert user != null;
        String token = tokenProvider.generate(user);
        log.info("LOGIN SUCCESS - userId: {} | username: {} | role: {}", user.getId(), user.getUsername(), user.getRole());
        return new LoginResponse(token);
    }

    public UserResponseDTO register(AuthRegisterDTO dto) {

        if (userRepository.findByUsername(dto.username()) != null){
            log.warn("REGISTRATION FAILED - username already exists: {}", dto.username());
            throw new RuntimeException("User already exists");
        }

        String encodedPassword = passwordEncoder.encode(dto.password());

        User user = User.builder()
                .name(dto.name())
                .username(dto.username())
                .password(encodedPassword)
                .role(UserRoles.USER).build();
        User saved = userRepository.save(user);
        log.info("REGISTRATION SUCCESS - userId: {} | username: {} | role: {}", saved.getId(), saved.getUsername(), saved.getRole());
        return UserResponseDTO.builder().name(saved.getName()).username(saved.getUsername()).build();
    }
}
