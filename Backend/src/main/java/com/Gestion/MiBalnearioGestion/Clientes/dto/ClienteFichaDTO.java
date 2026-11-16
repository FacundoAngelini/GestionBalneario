package com.Gestion.MiBalnearioGestion.Clientes.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;

public record ClienteFichaDTO(
        @JsonUnwrapped ClienteResponse cliente,
        String nombreUsuario,
        List<ReservaResumenDTO> reservasActivas,
        int reservasAnteriores,
        List<ReservaResumenDTO> ultimasAnteriores
) {}