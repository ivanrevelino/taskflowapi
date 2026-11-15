package com.ivan.taskflowapi.security.jwt;

import com.ivan.taskflowapi.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JWTTokenProviderTest {

    private final JWTTokenProvider tokenProvider = new JWTTokenProvider();

    JWTTokenProviderTest() {
        ReflectionTestUtils.setField(tokenProvider, "secret", "a-secret-that-is-long-enough-for-hmac");
        ReflectionTestUtils.setField(tokenProvider, "expiration", 60L);
    }

    @Test
    void generateShouldCreateTokenThatVerifies() {
        User user = User.builder().username("member01").build();

        String token = tokenProvider.generate(user);

        assertThat(tokenProvider.verify(token)).isEqualTo("member01");
    }

    @Test
    void verifyShouldThrowAuthenticationExceptionForInvalidToken() {
        assertThatThrownBy(() -> tokenProvider.verify("not-a-jwt"))
                .isInstanceOf(InvalidJwtAuthenticationException.class)
                .hasMessage("Invalid or expired JWT");
    }
}
