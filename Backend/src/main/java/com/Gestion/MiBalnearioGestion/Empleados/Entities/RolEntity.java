package com.Gestion.MiBalnearioGestion.Empleados.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

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

    @Column(name="public_id", unique=true, nullable=false, updatable = false)
    @UuidGenerator
    private UUID publicId;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_rol", nullable = false)
    private EtipoRol tipoRol;

    @OneToMany(mappedBy = "rol")
    @JsonIgnoreProperties("rol")
    private List<EmpleadoEntity> empleados;


}
