package com.Gestion.MiBalnearioGestion.Empleados.Entities;

import com.Gestion.MiBalnearioGestion.Empleados.EtipoRol;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name="rol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_rol", nullable = false)
    private EtipoRol tipoRol;

    @OneToMany(mappedBy = "rol")
    private List<EmpleadoEntity> empleados;


}
