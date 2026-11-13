package com.Gestion.MiBalnearioGestion.Clientes.dto;

import com.Gestion.MiBalnearioGestion.Clientes.Entity.EAccionCliente;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventoClienteDTO(
        UUID publicId, EAccionCliente accion, String usuario, String empleado,
        LocalDateTime fecha, String motivo, String detalle) {}