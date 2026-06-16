package com.Gestion.MiBalnearioGestion.Clientes.Entity;

import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.Entity.UsuarioEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private UUID publicId;

    @Column(name="dni",unique = true,nullable = false)
    private int dni;

    @Column(name="nombre", nullable = false)
    private String nombre;

    @Column(name="apellido", nullable = false)
    private String apellido;

    @Column(name="email", unique = true, nullable = false)
    private String email;

    @Column(name="telefono", unique = true, nullable = false)
    private String telefono;

    @Column(name="fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Column(name="estado", nullable = false)
    private boolean estado;

    @OneToMany(mappedBy = "cliente")
    private List<ReservaEntity> reservas = new ArrayList<>();

    @OneToOne
    @JoinColumn(name="usuario_id", nullable= false)
    private UsuarioEntity usuario;


}
