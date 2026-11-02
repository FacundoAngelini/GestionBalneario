package com.Gestion.MiBalnearioGestion.Recursos.Entity;

import com.Gestion.MiBalnearioGestion.Recursos.Enum.ETipoCancha;
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
    @Enumerated(EnumType.STRING)
    @Column(name="tipo_cancha", nullable = false)
    private ETipoCancha tipoCancha;

    @Column(name="capacidad", nullable = false)
    private int capacidad;

    @Column(name="iluminacion", nullable = false)
    private boolean iluminacion;

}
