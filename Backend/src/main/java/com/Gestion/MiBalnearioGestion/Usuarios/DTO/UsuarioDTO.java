package com.Gestion.MiBalnearioGestion.Usuarios.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO que representa la información de un usuario")
public class UsuarioDTO {

    @Schema(description = "Identificador único del cliente asociado al usuario", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID clienteId;

    @Schema(description = "Nombre de cuenta o alias del usuario en el sistema", example = "juan.perez")
    @Size(max = 30, message = "El nombre de usuario no puede contener mas de 30 caracteres")
    private String nombreUsuario;

    @Schema(description = "Conjunto de roles o permisos asignados al usuario", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private Set<String> roles;
}