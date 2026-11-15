package com.ivan.taskflowapi.security.filters;

import com.ivan.taskflowapi.repository.UserRepository;
import com.ivan.taskflowapi.security.jwt.InvalidJwtAuthenticationException;
import com.ivan.taskflowapi.security.jwt.JWTTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private static final HttpStatusEntryPoint UNAUTHORIZED_ENTRY_POINT =
            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);

    private final JWTTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = recoverToken(request);

        if (token != null) {
            String username;
            try {
                username = tokenProvider.verify(token);
            } catch (InvalidJwtAuthenticationException exception) {
                rejectAuthentication(request, response, exception);
                return;
            }

            UserDetails user = userRepository.findByUsername(username);
            if (user == null) {
                rejectAuthentication(request, response,
                        new InvalidJwtAuthenticationException("User associated with JWT was not found"));
                return;
            }

            var authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private void rejectAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        UNAUTHORIZED_ENTRY_POINT.commence(request, response, exception);
    }

    private String recoverToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }

        return header.replace("Bearer ", "");
    }
}
