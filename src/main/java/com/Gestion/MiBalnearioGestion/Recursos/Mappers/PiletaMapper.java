package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.*;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CocheraDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.PiletaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CocheraEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PiletaEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@RequiredArgsConstructor
public class PiletaMapper implements IMapper<PiletaEntity, PiletaDTO> {

    @Override
    public PiletaDTO convertToDTO(PiletaEntity entity) {
        PiletaDTO dto = new PiletaDTO();
        dto.setPublicId(entity.getPublicId());
        dto.setNombre(entity.getNombre());
        dto.setEsReservable(entity.isEsReservable());
        if (entity.getSector() != null) {
            dto.setSectorPublicId(entity.getSector().getPublicId());
        }
        dto.setEsClimatizada(entity.isEsClimatizada());
        dto.setTamanio(entity.getTamanio());
        return dto;
    }

    @Override
    public PiletaEntity convertToEntity(PiletaDTO dto, Class<PiletaEntity> clase) {
        PiletaEntity entity = new PiletaEntity();
        entity.setNombre(dto.getNombre());
        entity.setEsReservable(dto.getEsReservable() != null && dto.getEsReservable());
        entity.setEsClimatizada(dto.getEsClimatizada() != null && dto.getEsClimatizada());
        entity.setTamanio(dto.getTamanio() != null ? dto.getTamanio() : 0);
        return entity;
    }

    public void updateEntityFromDTO(PiletaDTO dto, PiletaEntity entity) {
        if (dto.getNombre() != null) entity.setNombre(dto.getNombre());
        if (dto.getEsReservable() != null) entity.setEsReservable(dto.getEsReservable());
        if (dto.getEsClimatizada() != null) entity.setEsClimatizada(dto.getEsClimatizada());
        if (dto.getTamanio() != null) entity.setTamanio(dto.getTamanio());
    }
}

