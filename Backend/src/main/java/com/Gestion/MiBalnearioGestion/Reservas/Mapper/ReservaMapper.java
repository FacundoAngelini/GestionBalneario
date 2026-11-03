package com.Gestion.MiBalnearioGestion.Reservas.Mapper;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.*;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservaMapper implements IMapper<ReservaEntity, ReservaDTO> {
    private final ModelMapper modelMapper;

    @Override
    public ReservaEntity convertToEntity (ReservaDTO dto, Class<ReservaEntity> entityClass) {
        return modelMapper.map(dto, ReservaEntity.class);
    }

    @Override
    public ReservaDTO convertToDTO(ReservaEntity entity) {
        ReservaDTO dto = modelMapper.map(entity, ReservaDTO.class);
        if (entity.getRecursos() != null) {
            List<UUID> ids = entity.getRecursos().stream()
                    .map(RecursoEntity::getPublicId)
                    .toList();
            dto.setRecursosPublicIds(ids);
        }
        return dto;
    }

    public void updateEntityFromDTO (ReservaDTO dto, ReservaEntity entity) {
        modelMapper.map(dto, entity);
    }
}
