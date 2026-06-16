package com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "DTO de solicitud utilizado cuando un usuario olvida sus credenciales y requiere iniciar el proceso de recuperación de cuenta de forma pública")
public record ContraseniaOlvidadaDTO(

        @Schema(
                description = "Nombre de usuario único registrado en el sistema (Generalmente el correo electrónico o el identificador asignado)",
                example = "mgomez.2026@example.com"
        )
        @NotBlank(message = "El nombre de usuario no puede estar vacío")
        String nombreUsuario
) {}