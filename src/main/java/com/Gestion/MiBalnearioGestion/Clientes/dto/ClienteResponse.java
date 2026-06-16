package com.Gestion.MiBalnearioGestion.Clientes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de respuesta que expone la información del perfil público y estado de cuenta de un cliente")
public class ClienteResponse {

    @Schema(description = "UUID público único del cliente en la plataforma", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
    private UUID publicId;

    @Schema(description = "Nombre o nombres del cliente", example = "Mariana")
    private String nombre;

    @Schema(description = "Apellido o apellidos del cliente", example = "Gómez")
    private String apellido;

    @Schema(description = "Documento Nacional de Identidad", example = "38123456")
    private Integer dni;

    @Schema(description = "Dirección de correo electrónico registrada", example = "mariana.gomez@example.com")
    private String email;

    @Schema(description = "Número telefónico de contacto", example = "2236987654")
    private String telefono;

    @Schema(description = "Fecha exacta en la que el cliente se registró en el sistema (Format: YYYY-MM-DD)", example = "2026-06-16")
    private LocalDate fechaAlta;

    @Schema(description = "Indica si la cuenta del cliente se encuentra activa y habilitada para operar", example = "true")
    private Boolean estado;
}
