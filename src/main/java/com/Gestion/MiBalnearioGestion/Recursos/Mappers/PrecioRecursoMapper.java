package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.*;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.PrecioRecursoDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class PrecioRecursoMapper implements IMapper<PrecioRecursoEntity, PrecioRecursoDTO> {

    @Override
    public PrecioRecursoDTO convertToDTO(PrecioRecursoEntity entity) {
        PrecioRecursoDTO dto = new PrecioRecursoDTO();
        dto.setPublicId(entity.getPublicId());
        dto.setPrecio(entity.getPrecio());
        dto.setFechaVigencia(entity.getFechaVigencia());
        dto.setFechaCaducada(entity.getFechaCaducada());
        if (entity.getRecurso() != null)
            dto.setRecursoPublicId(entity.getRecurso().getPublicId());
        return dto;
    }

    @Override
    public PrecioRecursoEntity convertToEntity(PrecioRecursoDTO dto, Class<PrecioRecursoEntity> clase) {
        PrecioRecursoEntity entity = new PrecioRecursoEntity();
        entity.setPrecio(dto.getPrecio());
        entity.setFechaVigencia(dto.getFechaVigencia());
        entity.setFechaCaducada(dto.getFechaCaducada());
        // recurso lo resuelve el service con recursoPublicId
        return entity;
    }

    public void updateToEntityFromDTO(PrecioRecursoDTO dto, PrecioRecursoEntity entity) {
        if (dto.getPrecio() > 0) entity.setPrecio(dto.getPrecio());
        if (dto.getFechaVigencia() != null) entity.setFechaVigencia(dto.getFechaVigencia());
        if (dto.getFechaCaducada() != null) entity.setFechaCaducada(dto.getFechaCaducada());
        // recurso no se toca acá, lo maneja el service
    }
}