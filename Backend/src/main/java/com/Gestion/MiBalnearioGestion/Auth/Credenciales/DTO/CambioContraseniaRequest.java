package com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO de solicitud para el cambio de clave de seguridad del usuario")
public record CambioContraseniaRequest (

        @Schema(description = "Contraseña vigente del usuario", example = "MiClaveVieja123!")
        @NotBlank(message = "La contraseña actual es obligatoria")
        String contraseniaActual,

        @Schema(
                description = "Nueva clave. Requisitos: Entre 8 y 20 caracteres, mínimo una mayúscula, una minúscula, un número y un carácter especial (@$!%*?&)",
                example = "NuevaClave2026!"
        )
        @NotBlank(message = "La nueva contraseña no puede estar vacía")
        @Size(min = 8, max = 20, message = "La nueva contraseña debe tener entre 8 y 20 caracteres")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d[@$!%*?&]]{8,20}$",
                message = "La nueva contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial (@$!%*?&)"
        )
        String nuevaContrasenia
) {}