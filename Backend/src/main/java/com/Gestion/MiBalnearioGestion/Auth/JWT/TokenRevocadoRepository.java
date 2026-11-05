package com.Gestion.MiBalnearioGestion.Auth.JWT;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TokenRevocadoRepository extends JpaRepository<TokenRevocadoEntity, Long> {
    boolean existsByTokenHash(String tokenHash);
    void deleteByExpiraBefore(LocalDateTime fecha);
}