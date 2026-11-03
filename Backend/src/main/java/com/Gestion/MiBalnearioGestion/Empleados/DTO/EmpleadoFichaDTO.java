package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ficha de un empleado, con su usuario, su rol de seguridad y si quien consulta puede modificarlo")
public record EmpleadoFichaDTO(
        @JsonUnwrapped EmpleadoResponseDTO empleado,
        String nombreUsuario,
        String rolSeguridad,
        boolean editable
) {}