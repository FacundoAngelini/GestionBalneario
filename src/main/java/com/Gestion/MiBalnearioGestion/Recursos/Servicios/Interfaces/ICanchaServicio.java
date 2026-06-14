package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.CanchaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.ETipoCancha;

import java.util.List;
import java.util.UUID;

public interface ICanchaServicio {
    CanchaDTO crearCancha(CanchaDTO canchaDTO);
    CanchaDTO buscarPorId(UUID id);
    CanchaDTO actualizarCancha(UUID id, CanchaDTO canchaDTO);
    List<CanchaDTO> buscarTodas(ETipoCancha cancha,
                                Integer capacidadIgual,
                                Integer capacidadMenor,
                                Integer capacidadMayor,
                                Boolean iluminacion);
}
