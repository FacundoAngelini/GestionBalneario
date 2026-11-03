package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EAccionEmpleado;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventoEmpleadoDTO(
        UUID publicId, EAccionEmpleado accion, String usuario, String empleado,
        LocalDateTime fecha, String motivo, String detalle) {}