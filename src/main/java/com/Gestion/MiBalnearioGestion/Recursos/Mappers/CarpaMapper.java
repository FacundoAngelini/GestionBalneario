package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.*;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CanchaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CarpaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CanchaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CarpaEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarpaMapper implements IMapper<CarpaEntity, CarpaDTO> {
    @Override
    public CarpaDTO convertToDTO(CarpaEntity entity) {
        CarpaDTO dto = new CarpaDTO();
        dto.setPublicId(entity.getPublicId());
        dto.setNombre(entity.getNombre());
        dto.setEsReservable(entity.isEsReservable());
        if (entity.getSector() != null) dto.setSectorPublicId(entity.getSector().getPublicId());
        dto.setNumero(entity.getNumero());
        dto.setPasillo(entity.getPasillo());
        dto.setCapacidad(entity.getCapacidad());
        return dto;
    }

    @Override
    public CarpaEntity convertToEntity(CarpaDTO dto, Class<CarpaEntity> clase) {
        CarpaEntity entity = new CarpaEntity();
        entity.setNombre(dto.getNombre());
        entity.setEsReservable(dto.getEsReservable() != null && dto.getEsReservable());
        entity.setNumero(dto.getNumero());
        entity.setPasillo(dto.getPasillo());
        entity.setCapacidad(dto.getCapacidad());
        return entity;
    }

    public void updateEntityFromDTO(CarpaDTO dto, CarpaEntity entity) {
        if (dto.getNombre() != null) entity.setNombre(dto.getNombre());
        if (dto.getEsReservable() != null) entity.setEsReservable(dto.getEsReservable());
        if (dto.getNumero() > 0) entity.setNumero(dto.getNumero());
        if (dto.getPasillo() > 0) entity.setPasillo(dto.getPasillo());
        if (dto.getCapacidad() > 0) entity.setCapacidad(dto.getCapacidad());
    }
}
