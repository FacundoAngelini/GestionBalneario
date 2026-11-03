package com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "DTO de solicitud utilizado para establecer una nueva contraseña utilizando el token de verificación recibido por correo")
public record ResetearContraseniaDTO(

        @Schema(
                description = "Token único y temporal de verificación enviado al correo electrónico del usuario",
                example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"
        )
        @NotBlank(message = "El token de verificación es obligatorio")
        String token,

        @Schema(
                description = "Nueva clave de acceso. Requisitos: Entre 8 y 20 caracteres, mínimo una mayúscula, una minúscula, un número y un carácter especial (@$!%*?&)",
                example = "NuevaClaveSegura2026!"
        )
        @NotBlank(message = "La nueva contraseña no puede estar vacía")
        @Size(min = 8, max = 20, message = "La nueva contraseña debe tener entre 8 y 20 caracteres")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d[@$!%*?&]]{8,20}$",
                message = "La nueva contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial (@$!%*?&)"
        )
        String nuevaContrasenia
) {}