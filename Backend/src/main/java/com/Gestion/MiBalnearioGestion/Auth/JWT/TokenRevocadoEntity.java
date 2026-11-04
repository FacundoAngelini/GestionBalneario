package com.Gestion.MiBalnearioGestion.Auth.JWT;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** Cuando se cierra la sesión, se guarda su hash, nunca el token. */
@Entity
@Table(name = "token_revocado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRevocadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    /** Cuándo vence el token: pasado ese momento ya no hace falta recordarlo. */
    @Column(nullable = false)
    private LocalDateTime expira;
}