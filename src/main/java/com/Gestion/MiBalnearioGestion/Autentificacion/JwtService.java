package com.Gestion.MiBalnearioGestion.Autentificacion;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
//LA HICE BASTANTE RÁPIDO A CHECKEAR EL J UEVES 28
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationTime;

    //Lo lee del yaml ahi lo configuramos
    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration}") long expiration) {

        // The secret must have at least 32 characters (256 bits) for HS256 [citation:10]
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expiration;
    }

    // Falta agregar atributos
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username) // Usuario que creo el token -fran
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey) // firma con llave
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false; // si el token es invalido
        }
    }
}