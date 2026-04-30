package com.Gestion.MiBalnearioGestion.Recursos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Table(name="piletas")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PiletaEntity extends RecursoEntity {
    @Column(name="es_climatizada", nullable = false)
    private boolean esClimatizada;

    @Column(name="tamanio", nullable = false)
    private int tamanio;
}
