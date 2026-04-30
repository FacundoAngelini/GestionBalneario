package com.Gestion.MiBalnearioGestion.Recursos;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Table(name="sombrillas")
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SombrillaEntity extends RecursoEntity {

    @Column(name="numero_carpa", unique = true, nullable = false)
    private int numero;

    @Enumerated(EnumType.STRING)
    @Column(name="tamanio_carpa", nullable = false)
    private Etamanio_sombrilla tamanio;
}
