package com.Gestion.MiBalnearioGestion.Empleados.Entities;

import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

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

    @Column(name="public_id", unique=true, nullable=false, updatable = false)
    @UuidGenerator
    private String publicId;

    @Column(name="nombre_sector", unique = true, nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "sector")
    private List<RecursoEntity> recursos = new ArrayList<>();

    @OneToMany(mappedBy = "sector")
    private List<EmpleadoEntity> empleados;


}
