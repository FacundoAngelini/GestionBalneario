package com.Gestion.MiBalnearioGestion.Empleados.Entities;

import com.Gestion.MiBalnearioGestion.Empleados.EtipoRol;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

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

    @Column(name="public_id", unique=true, nullable=false, updatable = false)
    @UuidGenerator
    private String publicId;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_rol", nullable = false)
    private EtipoRol tipoRol;

    @OneToMany(mappedBy = "rol")
    private List<EmpleadoEntity> empleados;


}
