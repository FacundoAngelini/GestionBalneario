package com.Gestion.MiBalnearioGestion.Sector;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private UUID publicId;

    @Column(name="nombre_sector", unique = true, nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "sector")
    @JsonIgnoreProperties("sector")
    private List<RecursoEntity> recursos = new ArrayList<>();

    @OneToMany(mappedBy = "sector")
    @JsonIgnoreProperties("sector")
    private List<EmpleadoEntity> empleados;


}
