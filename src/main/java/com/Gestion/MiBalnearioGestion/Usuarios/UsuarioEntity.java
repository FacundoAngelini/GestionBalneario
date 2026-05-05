package com.Gestion.MiBalnearioGestion.Usuarios;

import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

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
    private String publicId;

    @Column(name="username", unique = true, nullable = false, updatable = false)
    private String username;

    @Column(name="password", nullable = false)
    private String password;

    @OneToOne(mappedBy = "usuario")
    private EmpleadoEntity empleado;

    @OneToOne(mappedBy = "usuario")
    private ClienteEntity cliente;
}
