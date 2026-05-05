package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Reservas.ReservaEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="public_id", unique=true, nullable=false, updatable = false)
    @UuidGenerator
    private String publicId;

    @Column(name="nombre", nullable = false)
    private String nombre;

    @Column(name="apellido", nullable = false)
    private String apellido;

    @Column(name="email", unique = true, nullable = false)
    private String email;

    @Column(name="contrasenia", nullable = false)
    private String contrasenia;

    @Column(name="telefono", unique = true, nullable = false)
    private String telefono;

    @Column(name="fecha_alta", nullable = false)
    private LocalDate fecha_alta;

    @Column(name="estado", nullable = false)
    private boolean estado;

    @OneToMany(mappedBy = "cliente")
    private List<ReservaEntity> reservas = new ArrayList<>();

}
