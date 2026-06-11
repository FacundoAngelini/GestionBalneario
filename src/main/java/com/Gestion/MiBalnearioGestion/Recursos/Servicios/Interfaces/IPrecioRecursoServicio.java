package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.PrecioRequestRecursoDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.PrecioRecursoResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IPrecioRecursoServicio {
    PrecioRecursoResponseDTO crearPrecio(PrecioRequestRecursoDTO dto);
    PrecioRecursoResponseDTO buscarPorPublicId(UUID publicId);
    List<PrecioRecursoResponseDTO> buscarPorRecurso(UUID recursoPublicId);
    List<PrecioRecursoResponseDTO> buscarTodos(LocalDate vigenciaIgual, LocalDate vigenciaMenor,
                                               LocalDate vigenciaMayor, LocalDate caducadaIgual,
                                               LocalDate caducadaMenor, LocalDate caducadaMayor,
                                               Double precioIgual, LocalDate precioMenor,
                                               Double precioMayor);
    void eliminarPrecio(UUID publicId);
}
