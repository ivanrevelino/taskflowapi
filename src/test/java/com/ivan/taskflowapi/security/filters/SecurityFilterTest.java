package com.ivan.taskflowapi.security.filters;

import com.ivan.taskflowapi.repository.UserRepository;
import com.ivan.taskflowapi.security.jwt.InvalidJwtAuthenticationException;
import com.ivan.taskflowapi.security.jwt.JWTTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SecurityFilterTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void invalidTokenShouldReturnUnauthorizedWithoutContinuingFilterChain() throws Exception {
        JWTTokenProvider tokenProvider = mock(JWTTokenProvider.class);
        UserRepository userRepository = mock(UserRepository.class);
        SecurityFilter securityFilter = new SecurityFilter(tokenProvider, userRepository);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid");
        MockHttpServletResponse response = new MockHttpServletResponse();
        var filterChain = mock(jakarta.servlet.FilterChain.class);

        when(tokenProvider.verify("invalid"))
                .thenThrow(new InvalidJwtAuthenticationException("Invalid or expired JWT"));

        securityFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        verifyNoInteractions(userRepository);
        verify(filterChain, never()).doFilter(any(), any());
    }
}
