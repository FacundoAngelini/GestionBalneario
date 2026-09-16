package com.Gestion.MiBalnearioGestion.Usuarios.Entity;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Entity.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Clientes.Entity.ClienteEntity;
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
    private CredencialEntity credencial;

    @OneToOne(mappedBy = "usuario")
    private EmpleadoEntity empleado;

    @OneToOne(mappedBy = "usuario")
    private ClienteEntity cliente;
}
