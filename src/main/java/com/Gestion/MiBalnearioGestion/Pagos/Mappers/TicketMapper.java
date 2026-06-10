package com.Gestion.MiBalnearioGestion.Pagos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoReservaDTO;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.TicketDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoReservaEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketMapper implements IMapper<TicketEntity, TicketDTO> {

    private final ModelMapper modelMapper;

    @Override
    public TicketDTO convertToDTO(TicketEntity entity) {
        return modelMapper.map(entity, TicketDTO.class);
    }

    @Override
    public TicketEntity convertToEntity(TicketDTO dto, Class<TicketEntity> entityClass) {
        return modelMapper.map(dto, TicketEntity.class);
    }

    public void updateEntityFromDTO(TicketDTO dto, TicketEntity entity) {
        modelMapper.map(dto, entity);
    }
}