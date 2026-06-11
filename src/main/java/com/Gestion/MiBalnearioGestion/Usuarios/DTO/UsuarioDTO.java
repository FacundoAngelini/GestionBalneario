package com.Gestion.MiBalnearioGestion.Usuarios.DTO;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteDTO;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponseDTO;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
    public class UsuarioDTO {
    private UUID publicId;
    private String nombreUsuario;   // viene de CredencialEntity
    private boolean activo;
    private ClienteResponseDTO cliente;    // null si es empleado
    private EmpleadoDTO empleado;
}
