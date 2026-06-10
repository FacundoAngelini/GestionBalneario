package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.IMapper;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.RecursoDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RecursoMapper implements IMapper<RecursoEntity, RecursoDTO> {
    private final ModelMapper modelMapper;

    @Override
    public RecursoEntity convertToEntity(RecursoDTO recursoDTO, Class<RecursoEntity> recursoEntityClass){
        return modelMapper.map(recursoDTO, RecursoEntity.class);
    }

    @Override
    public RecursoDTO convertToDTO(RecursoEntity recursoEntity) {
        return modelMapper.map(recursoEntity, RecursoDTO.class);
    }

    public void updateEntityFromDTO(RecursoDTO recursoDTO, RecursoEntity recursoEntity){
        modelMapper.map(recursoDTO, recursoEntity);
    }

}
