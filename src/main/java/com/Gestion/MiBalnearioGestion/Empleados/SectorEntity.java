package com.Gestion.MiBalnearioGestion.Empleados;

import com.Gestion.MiBalnearioGestion.Recursos.RecursoEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="sectores")
@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
public class SectorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="nombre_sector", unique = true, nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "sector")
    private List<RecursoEntity> recursos = new ArrayList<>();

    @OneToMany(mappedBy = "sector")
    private List<EmpleadoEntity> empleados;


}
