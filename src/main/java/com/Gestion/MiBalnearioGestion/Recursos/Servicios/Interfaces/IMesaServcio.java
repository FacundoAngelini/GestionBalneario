package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.MesaDTO;

import java.util.List;
import java.util.UUID;

public interface IMesaServcio {
    MesaDTO crearMesa(MesaDTO dto);
    MesaDTO actualizarMesa(MesaDTO dto, UUID id);
    MesaDTO obtenerMesaId(UUID id);
    List<MesaDTO> obtenerMesas(Integer numeroIgual,
                               Integer numeroMenor,
                               Integer numeroMayor,
                               Integer capacidadIgual,
                               Integer capacidadMenor,
                               Integer capacidadMayor);
}
