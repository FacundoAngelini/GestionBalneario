package com.Gestion.MiBalnearioGestion.Clientes.dto;

import com.Gestion.MiBalnearioGestion.Auth.Autentificacion.DTO.AuthRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de entrada para el autorregistro o creación de una nueva cuenta de cliente en la plataforma")
public class ClienteRequest {

    @Schema(description = "Nombre o nombres del cliente (Entre 2 y 50 caracteres)", example = "Mariana")
    @NotBlank(message = "Debe ingresar un nombre válido")
    @Size(min = 2, max = 50)
    private String nombre;

    @Schema(description = "Apellido o apellidos del cliente (Entre 2 y 50 caracteres)", example = "Gómez")
    @NotBlank(message = "Debe ingresar un apellido válido")
    @Size(min = 2, max = 50)
    private String apellido;

    @Schema(description = "Documento Nacional de Identidad único del usuario", example = "38123456")
    @NotNull
    private Integer dni;

    @Schema(description = "Dirección de correo electrónico con formato estándar en minúsculas", example = "mariana.gomez@example.com")
    @NotBlank
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            message = "El email ingresado tiene un formato inválido")
    private String email;

    @Schema(description = "Número telefónico móvil o de contacto (Solo caracteres numéricos)", example = "2236987654")
    @NotBlank
    @Pattern(regexp = "^[0-9]+$", message = "El teléfono debe contener solo números")
    private String telefono;

    @Schema(description = "Credenciales de acceso iniciales (Nombre de usuario único y contraseña cifrada) requeridas para el login")
    @Valid
    @NotNull(message = "Las credenciales no pueden estar vacías")
    private AuthRequest credencial;
}