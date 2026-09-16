package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.*;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CarpaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CocheraDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CarpaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CocheraEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CocheraMapper implements IMapper <CocheraEntity, CocheraDTO> {
    private final ModelMapper modelMapper;

    @Override
    public CocheraDTO convertToDTO(CocheraEntity entity) {
        CocheraDTO dto = new CocheraDTO();
        dto.setPublicId(entity.getPublicId());
        dto.setNombre(entity.getNombre());
        dto.setEsReservable(entity.isEsReservable());
        if (entity.getSector() != null) dto.setSectorPublicId(entity.getSector().getPublicId());
        dto.setNumeroCochera(entity.getNumeroCochera());
        return dto;
    }

    @Override
    public CocheraEntity convertToEntity(CocheraDTO dto, Class<CocheraEntity> clase) {
        CocheraEntity entity = new CocheraEntity();
        entity.setNombre(dto.getNombre());
        entity.setEsReservable(dto.getEsReservable() != null && dto.getEsReservable());
        entity.setNumeroCochera(dto.getNumeroCochera());
        return entity;
    }

    public void updateEntityFromDTO(CocheraDTO dto, CocheraEntity entity) {
        if (dto.getNombre() != null) entity.setNombre(dto.getNombre());
        if (dto.getEsReservable() != null) entity.setEsReservable(dto.getEsReservable());
        if (dto.getNumeroCochera() > 0) entity.setNumeroCochera(dto.getNumeroCochera());
    }
}
