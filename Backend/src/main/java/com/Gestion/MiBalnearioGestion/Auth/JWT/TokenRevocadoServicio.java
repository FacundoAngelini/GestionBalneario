package com.Gestion.MiBalnearioGestion.Auth.JWT;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class TokenRevocadoServicio {

    private final TokenRevocadoRepository repositorio;
    private final JwtService jwtService;

    @Transactional
    public void revocar(String token) {
        String hash = hash(token);
        if (repositorio.existsByTokenHash(hash)) return;
        Date vence = jwtService.extractExpiration(token);
        repositorio.save(TokenRevocadoEntity.builder()
                .tokenHash(hash)
                .expira(vence.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build());
    }

    public boolean estaRevocado(String token) {
        return repositorio.existsByTokenHash(hash(token));
    }

    /** Los tokens vencidos ya no se aceptan de todos modos: se borran para que la tabla no crezca. */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void limpiarVencidos() {
        repositorio.deleteByExpiraBefore(LocalDateTime.now());
    }

    private static String hash(String token) {
        try {
            byte[] resumen = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(resumen);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}