package com.ivan.taskflowapi.service;

import com.ivan.taskflowapi.models.RefreshToken;
import com.ivan.taskflowapi.models.User;
import com.ivan.taskflowapi.repository.RefreshTokenRepository;
import com.ivan.taskflowapi.security.jwt.JWTTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JWTTokenProvider jwtService;
    private final RefreshTokenRepository repository;

    public RefreshToken create(User user) {

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .user(user)
                .build();

        return repository.save(refreshToken);
    }

    public RefreshToken verify(String refreshToken) {

        RefreshToken token = repository.findByToken(refreshToken);

        if (token == null) {
            throw new RuntimeException("RefreshToken does not exists");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh Token expired. Make login to get new token");
        }

        return token;
    }

}
