package com.Gestion.MiBalnearioGestion.Recursos.Entity;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.SectorEntity;
import com.Gestion.MiBalnearioGestion.Reservas.ReservaEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Recurso")
@Inheritance(strategy = InheritanceType.JOINED) // une las tablas hijas para que compartan la misma pk, sin necesidad de hacer onetomay ya que esto lo hace atuomatico, puede ser single pero vana quedar muchas celdas nulas
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RecursoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="public_id", unique=true, nullable=false, updatable = false)
    @UuidGenerator
    private String publicId;

    @Column(name= "nombre_recurso",nullable = false)
    private String nombre;

    @Column(name="recurso_activo", nullable = false)
    private boolean esReservable;

    @ManyToOne
    @JoinColumn(name="sector_id", nullable = false)
    private SectorEntity sector; // aca quedaa la fk

    @ManyToMany(mappedBy = "recursos")
    private List<ReservaEntity> reservas=new ArrayList<>();

    @OneToMany(mappedBy = "recurso")
    private List<PrecioRecursoEntity> precioRecurso = new ArrayList<>();


}
