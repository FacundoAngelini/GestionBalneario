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
public class PrecioRecursoMapper implements IMapper<PrecioRecursoEntity,PrecioRecursoDTO> {
    private final ModelMapper modelMapper;

    @Override
    public PrecioRecursoEntity convertToEntity(PrecioRecursoDTO dto, Class<PrecioRecursoEntity> entityClass) {
        return modelMapper.map(dto, PrecioRecursoEntity.class);
    }

    @Override
    public PrecioRecursoDTO convertToDTO(PrecioRecursoEntity entity) {
        return modelMapper.map(entity, PrecioRecursoDTO.class);
    }

    public void updateToEntityFromDTO(PrecioRecursoDTO dto, PrecioRecursoEntity entity) {
        modelMapper.map(dto,entity);
    }
}
