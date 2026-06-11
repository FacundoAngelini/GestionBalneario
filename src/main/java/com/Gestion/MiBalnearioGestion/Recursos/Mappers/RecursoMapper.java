package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Request.RecursoRequestDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.PrecioRecursoResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.Response.RecursoResponseDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class RecursoMapper {

    public RecursoResponseDTO convertToDTO(RecursoEntity entity) {
        if (entity == null) return null;

        RecursoResponseDTO dto = new RecursoResponseDTO();
        dto.setPublicId(entity.getPublicId());
        dto.setNombre(entity.getNombre());
        dto.setEsReservable(entity.isEsReservable());

        if (entity.getSector() != null) {
            dto.setSectorPublicId(entity.getSector().getPublicId());
            dto.setSectorNombre(entity.getSector().getNombre());
        }

        if (entity.getPrecioRecurso() != null) {
            dto.setPrecios(entity.getPrecioRecurso().stream()
                    .map(this::mapPrecio)
                    .toList());
        }

        return dto;
    }

    private PrecioRecursoResponseDTO mapPrecio(PrecioRecursoEntity p) {
        PrecioRecursoResponseDTO dto = new PrecioRecursoResponseDTO();
        dto.setPublicId(p.getPublicId());
        dto.setPrecio(p.getPrecio());
        dto.setFechaVigencia(p.getFechaVigencia());
        dto.setFechaCaducada(p.getFechaCaducada());
        if (p.getRecurso() != null) dto.setRecursoPublicId(p.getRecurso().getPublicId());
        return dto;
    }
}
