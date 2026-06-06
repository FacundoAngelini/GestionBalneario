package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.PiletaDTO;

import java.util.List;
import java.util.UUID;

public interface IPiletaServicio {
    PiletaDTO crearPileta(PiletaDTO dto);
    PiletaDTO actualizarPileta(PiletaDTO dto, UUID id);
    PiletaDTO obtenerPileta(UUID id);
    List<PiletaDTO> obtenerPiletas(boolean climatizada,
                                   boolean noClimatizada,
                                   Integer tamanioIgual,
                                   Integer TamanioMayor,
                                   Integer TamanioMenor);
}
