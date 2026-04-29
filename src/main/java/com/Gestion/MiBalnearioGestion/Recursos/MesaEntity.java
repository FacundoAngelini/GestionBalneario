package com.Gestion.MiBalnearioGestion.Recursos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Table(name="Mesas")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MesaEntity extends RecursoEntity {
    @Column(name="numero_mesa", unique = true, nullable = false)
    private int numero;

    @Column(name="capacidad_mesa", nullable = false)
    private int capacidad;
}
