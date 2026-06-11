package com.Gestion.MiBalnearioGestion.Usuarios;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Table(name="usuarios")
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="public_id", unique=true, nullable=false, updatable = false)
    @UuidGenerator
    private UUID publicId;

    //@Column(name="nombreUsuario", unique = true, nullable = false, updatable = false)
    //private String nombreUsuario;
//
    //@Column(name="contrasenia", nullable = false)
    //private String contrasenia;

    @OneToOne(mappedBy = "usuario")
    private CredencialEntity credencial;

    @OneToOne(mappedBy = "usuario")
    private EmpleadoEntity empleado;

    @OneToOne(mappedBy = "usuario")
    private ClienteEntity cliente;
}
