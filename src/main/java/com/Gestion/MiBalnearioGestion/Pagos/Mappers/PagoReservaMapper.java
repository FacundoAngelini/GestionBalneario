package com.Gestion.MiBalnearioGestion.Pagos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoReservaDTO;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoReservaDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoReservaEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoReservaEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagoReservaMapper implements IMapper<PagoReservaEntity, PagoReservaDTO> {

    private final ModelMapper modelMapper;

    @Override
    public PagoReservaDTO convertToDTO(PagoReservaEntity entity) {
        return modelMapper.map(entity, PagoReservaDTO.class);
    }

    @Override
    public PagoReservaEntity convertToEntity(PagoReservaDTO dto, Class<PagoReservaEntity> entityClass) {
        return modelMapper.map(dto, PagoReservaEntity.class);
    }

    public void updateEntityFromDTO(PagoReservaDTO dto, PagoReservaEntity entity) {
        modelMapper.map(dto, entity);
    }
}
