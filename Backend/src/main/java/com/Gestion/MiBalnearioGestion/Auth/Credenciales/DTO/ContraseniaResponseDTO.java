package com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "DTO de respuesta unificado que retorna confirmaciones textuales sobre operaciones de seguridad y credenciales")
public record ContraseniaResponseDTO (

        @Schema(
                description = "Mensaje descriptivo del resultado de la operación de seguridad procesada por el servidor",
                example = "Se ha enviado un correo de recuperación con las instrucciones a la dirección registrada."
        )
        String mensaje
) {}