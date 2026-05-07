package com.Gestion.MiBalnearioGestion.Empleados.Entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class DireccionEntity {

    @Column(name = "calle", nullable = false)
    private String calle;

    @Column(name = "numero", nullable = false)
    private int numero;

    @Column(name = "ciudad", nullable = false)
    private String ciudad;

    @Column(name = "provincia", nullable = false)
    private String provincia;
}
