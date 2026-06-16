package com.Gestion.MiBalnearioGestion.Recursos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.IMapper;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.CanchaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.CanchaEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CanchaMapper implements IMapper<CanchaEntity, CanchaDTO> {
    private final ModelMapper modelMapper;

    @Override
    public CanchaDTO convertToDTO(CanchaEntity entity) {
        CanchaDTO dto = new CanchaDTO();
        dto.setPublicId(entity.getPublicId());
        dto.setNombre(entity.getNombre());
        dto.setEsReservable(entity.isEsReservable());
        if (entity.getSector() != null) dto.setSectorPublicId(entity.getSector().getPublicId());
        dto.setTipoCancha(entity.getTipoCancha());
        dto.setCapacidad(entity.getCapacidad());
        dto.setIluminacion(entity.isIluminacion());
        return dto;
    }

    @Override
    public CanchaEntity convertToEntity(CanchaDTO dto, Class<CanchaEntity> clase) {
        CanchaEntity entity = new CanchaEntity();
        entity.setNombre(dto.getNombre());
        entity.setEsReservable(dto.getEsReservable() != null && dto.getEsReservable());
        entity.setTipoCancha(dto.getTipoCancha());
        entity.setCapacidad(dto.getCapacidad());
        entity.setIluminacion(dto.getIluminacion());
        return entity;
    }

    public void updateEntityFromDTO(CanchaDTO dto, CanchaEntity entity) {
        if (dto.getNombre() != null) entity.setNombre(dto.getNombre());
        if (dto.getEsReservable() != null) entity.setEsReservable(dto.getEsReservable());
        if (dto.getTipoCancha() != null) entity.setTipoCancha(dto.getTipoCancha());
        if (dto.getCapacidad() > 0) entity.setCapacidad(dto.getCapacidad());
        entity.setIluminacion(dto.getIluminacion());
    }
}
