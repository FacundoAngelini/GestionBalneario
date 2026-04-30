package com.Gestion.MiBalnearioGestion.Recursos;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Table(name="canchas")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CanchaEntity extends RecursoEntity {
    @Enumerated(EnumType.STRING) // es mejor Sting y no oirdinal, ya que ordinal lo uarda como 0,1,2 y si luego lo camnio, se rompe todo
    @Column(name="tipo_cancha", nullable = false)
    private ETipoCancha tipoCancha;

    @Column(name="capacidad", nullable = false)
    private int capacidad;

    @Column(name="iluminacion", nullable = false)
    private boolean iluminacion;

}
