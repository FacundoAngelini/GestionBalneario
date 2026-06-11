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

    @OneToOne(mappedBy = "usuario")
    private EmpleadoEntity empleado;

    @Column(name="activo", nullable = false)
    private boolean activo = true;

    @OneToOne(mappedBy = "usuario")
    private ClienteEntity cliente;

    @OneToOne(mappedBy = "usuario")
    private CredencialEntity credencial;
}
