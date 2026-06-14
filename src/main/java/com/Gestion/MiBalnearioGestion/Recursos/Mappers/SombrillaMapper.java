package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.*;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.PiletaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.SombrillaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PiletaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.SombrillaEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

@Component
@RequiredArgsConstructor
public class SombrillaMapper implements IMapper<SombrillaEntity, SombrillaDTO>
{
    private final ModelMapper modelMapper;
    @Override
    public SombrillaDTO convertToDTO(SombrillaEntity entity) {
        SombrillaDTO dto = new SombrillaDTO();
        dto.setPublicId(entity.getPublicId());
        dto.setNombre(entity.getNombre());
        dto.setEsReservable(entity.isEsReservable());
        if (entity.getSector() != null)
            dto.setSectorPublicId(entity.getSector().getPublicId());
        dto.setNumero(entity.getNumero());
        dto.setTamanio(entity.getTamanio()); // ← mismo nombre
        return dto;
    }

    @Override
    public SombrillaEntity convertToEntity(SombrillaDTO dto, Class<SombrillaEntity> clase) {
        SombrillaEntity entity = new SombrillaEntity();
        entity.setNombre(dto.getNombre());
        entity.setEsReservable(dto.getEsReservable() != null && dto.getEsReservable());
        if (dto.getNumero() != null) entity.setNumero(dto.getNumero());
        entity.setTamanio(dto.getTamanio()); // ← mismo nombre
        return entity;
    }

    public void updateEntityFromDTO(SombrillaDTO dto, SombrillaEntity entity) {
        if (dto.getNombre() != null) entity.setNombre(dto.getNombre());
        if (dto.getEsReservable() != null) entity.setEsReservable(dto.getEsReservable());
        if (dto.getNumero() != null && dto.getNumero() > 0) entity.setNumero(dto.getNumero());
        if (dto.getTamanio() != null) entity.setTamanio(dto.getTamanio());
    }
    }

