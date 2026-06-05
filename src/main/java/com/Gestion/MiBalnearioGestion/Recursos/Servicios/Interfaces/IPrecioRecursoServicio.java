package com.Gestion.MiBalnearioGestion.Recursos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Recursos.DTO.PrecioRecursoDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IPrecioRecursoServicio {
    PrecioRecursoDTO crearPrecio(PrecioRecursoDTO dto);
    PrecioRecursoDTO buscarPorPublicId(UUID publicId);
    List<PrecioRecursoDTO> buscarTodos(LocalDate precioVigenciaIgual,
                                       LocalDate precioVigenciaMenor,
                                       LocalDate precioVigenciaMayor,
                                       LocalDate precioCaducidoIgual,
                                       LocalDate precioCaducidoMenor,
                                       LocalDate precioCaducidoMayor,
                                       Double precioIgual,
                                       Double precioMenor,
                                       Double precioMayor
    );
}
