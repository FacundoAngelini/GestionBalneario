package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.SombrillaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.EtamanioSombrilla;

import java.util.List;
import java.util.UUID;

public interface ISombrillaServicio {
    SombrillaDTO crearSombrilla(SombrillaDTO dto);
    SombrillaDTO actualizarSombrilla (SombrillaDTO dto, UUID id);
    SombrillaDTO buscarPorId(UUID id);
    List<SombrillaDTO> buscarTodas(Integer numero,
                                   Integer numeroMenor,
                                   Integer numeroMayor,
                                   EtamanioSombrilla tamanio);
}
