package com.Gestion.MiBalnearioGestion.Recursos.Entity;

import com.Gestion.MiBalnearioGestion.Sector.SectorEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private UUID publicId;

    @Column(name= "nombre_recurso",nullable = false)
    private String nombre;

    @Column(name="recurso_activo", nullable = false)
    private boolean esReservable;

    @ManyToOne
    @JoinColumn(name="sector_id", nullable = false)
    private SectorEntity sector;
    @ManyToMany(mappedBy = "recursos")
    @JsonBackReference
    private List<ReservaEntity> reservas = new ArrayList<>();

    @OneToMany(mappedBy = "recurso", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PrecioRecursoEntity> precioRecurso = new ArrayList<>();
}
