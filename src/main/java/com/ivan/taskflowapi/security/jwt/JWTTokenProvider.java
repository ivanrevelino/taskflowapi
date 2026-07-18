package com.ivan.taskflowapi.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.ivan.taskflowapi.models.User;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;

public class JWTTokenProvider {

    @Value("{jwt.secret}")
    String secret;

    @Value("{jwt.expiration}")
    long expiration;

    public String generate(User user) {

        Instant expirationTime = Instant.now().plusSeconds(expiration);

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getUsername())
                    .withExpiresAt(expirationTime)
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new JWTCreationException("Error while creating token", e);
        }
    }

    public String verificate(String token) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return "";
        }
    }

}
