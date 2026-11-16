package com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO;

import com.Gestion.MiBalnearioGestion.Auth.Validaciones.Telefono;
import com.Gestion.MiBalnearioGestion.Common.Validacion.TelefonoValido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de solicitud pública utilizado para el autoregistro de nuevos usuarios/clientes en la plataforma")
public class NewAccountRequest {

    @Schema(description = "Nombre de usuario único elegido para el inicio de sesión", example = "mariana.gomez")
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size (max=30, message = "El nuevo nombre de usuario no puede contener mas de 30 caracteres")
    private String nombreUsuario;

    @Schema(
            description = "Contraseña de acceso. Requisitos: Entre 8 y 20 caracteres, mínimo una mayúscula, una minúscula, un número y un carácter especial (@$!%*?&)",
            example = "Segura2026!"
    )
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, max = 20, message = "La contraseña debe tener entre 8 y 20 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d[@$!%*?&]]{8,20}$",
            message = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial (@$!%*?&)"
    )
    private String contrasenia;

    @Schema(description = "Nombre o nombres del usuario", example = "Mariana")
    @NotBlank(message = "El nombre es obligatorio")
    @Size (max=50, message = "El  nombre no puede contener mas de 50 caracteres")
    private String nombre;

    @Schema(description = "Apellido o apellidos del usuario", example = "Gómez")
    @NotBlank(message = "El apellido es obligatorio")
    @Size (max=50, message = "El  apellido no puede contener mas de 50 caracteres")
    private String apellido;

    @Schema(description = "Dirección de correo electrónico válida", example = "mariana.gomez@example.com")
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Size (max=75, message = "El email no puede contener mas de 75 caracteres")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "El email ingresado tiene un formato inválido")
    private String email;

    @Schema(description = "Teléfono con código de país, solo dígitos (ej: 542235123456)", example = "542235123456")
    @NotBlank @Pattern(regexp = "^[0-9]+$", message = "El teléfono debe contener solo números") @TelefonoValido
    private String telefono;

    @Schema(description = "Documento Nacional de Identidad", example = "38123456")
    @NotNull(message = "El DNI es obligatorio")
    @Min(value = 1_000_000, message = "El DNI no es válido")
    @Max(value = 99_999_999, message = "El DNI no es válido")
    private int dni;
}