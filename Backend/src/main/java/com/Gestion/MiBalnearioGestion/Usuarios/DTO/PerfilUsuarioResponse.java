package com.Gestion.MiBalnearioGestion.Usuarios.DTO;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponse;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Información del perfil del usuario autenticado")
public record PerfilUsuarioResponse(

        @Schema(description = "Nombre de usuario de la cuenta")
        String nombreUsuario,

        @Schema(description = "Roles de seguridad del usuario")
        List<String> roles,

        @Schema(description = "Tipo de perfil para presentar en el frontend")
        TipoPerfilUsuario tipoPerfil,

        @Schema(
                description = "Datos del cliente cuando existe un cliente asociado",
                nullable = true
        )
        ClienteResponse cliente,

        @Schema(
                description = "Datos del empleado cuando existe un empleado asociado",
                nullable = true
        )
        EmpleadoResponseDTO empleado

) {
}