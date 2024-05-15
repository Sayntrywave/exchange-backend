package com.korotkov.exchange.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JWTService {

    String secret;
    JWTVerifier verifier;


    public JWTService(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
        this.verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User details")
                .withIssuer("nikita")
                .build();
    }

    public String generateToken(String username) {
        return generateToken(username, "username", 60*24);
    }

    public String generateToken(String claim, String claimName) {
        return generateToken(claim, claimName, 60 * 24);
    }

    private String generateToken(String claim, String claimName, int expireIn) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(expireIn).toInstant());
        return JWT.create()
                .withSubject("User details")
                .withClaim(claimName, claim)
                .withIssuedAt(Instant.now())
                .withIssuer("nikita")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateTokenAndRetrieveClaim(String stringToken) {
        DecodedJWT jwt = verifier.verify(stringToken);
        return jwt.getClaim("username").asString();
    }

    public String validateTokenAndRetrieveClaim(String stringToken, String claimName) {
        DecodedJWT jwt = verifier.verify(stringToken);
        return jwt.getClaim(claimName).asString();
    }

}