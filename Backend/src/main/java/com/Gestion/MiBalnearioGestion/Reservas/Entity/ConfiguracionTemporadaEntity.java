package com.Gestion.MiBalnearioGestion.Reservas.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "configuracion_temporada")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionTemporadaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inicio_temporada", nullable = false)
    private LocalDate inicioTemporada;

    @Column(name = "fin_temporada", nullable = false)
    private LocalDate fin_temporada;
}