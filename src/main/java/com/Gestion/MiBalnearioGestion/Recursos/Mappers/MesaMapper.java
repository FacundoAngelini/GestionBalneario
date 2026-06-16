package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.*;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CarpaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CocheraDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.MesaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CarpaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CocheraEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.MesaEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MesaMapper implements IMapper<MesaEntity, MesaDTO> {
    @Override
    public MesaDTO convertToDTO(MesaEntity entity) {
        MesaDTO dto = new MesaDTO();
        dto.setPublicId(entity.getPublicId());
        dto.setNombre(entity.getNombre());
        dto.setEsReservable(entity.isEsReservable());
        if (entity.getSector() != null) dto.setSectorPublicId(entity.getSector().getPublicId());
        dto.setNumero(entity.getNumero());
        dto.setCapacidad(entity.getCapacidad());
        return dto;
    }

    @Override
    public MesaEntity convertToEntity(MesaDTO dto, Class<MesaEntity> clase) {
        MesaEntity entity = new MesaEntity();
        entity.setNombre(dto.getNombre());
        entity.setEsReservable(dto.getEsReservable() != null && dto.getEsReservable());
        entity.setNumero(dto.getNumero());
        entity.setCapacidad(dto.getCapacidad());
        return entity;
    }

    public void updateEntityFromDTO(MesaDTO dto, MesaEntity entity) {
        if (dto.getNombre() != null) entity.setNombre(dto.getNombre());
        if (dto.getEsReservable() != null) entity.setEsReservable(dto.getEsReservable());
        if (dto.getNumero() > 0) entity.setNumero(dto.getNumero());
        if (dto.getCapacidad() > 0) entity.setCapacidad(dto.getCapacidad());
    }
}
