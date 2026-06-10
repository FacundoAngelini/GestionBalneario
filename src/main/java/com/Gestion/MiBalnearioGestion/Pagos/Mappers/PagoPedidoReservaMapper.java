package com.Gestion.MiBalnearioGestion.Pagos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.*;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoMesaDTO;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoReservaDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoMesaEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoReservaEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagoPedidoReservaMapper implements IMapper<PagoPedidoReservaEntity, PagoPedidoReservaDTO> {

    private final ModelMapper modelMapper;

    @Override
    public PagoPedidoReservaDTO convertToDTO(PagoPedidoReservaEntity entity) {
        return modelMapper.map(entity, PagoPedidoReservaDTO.class);
    }

    @Override
    public PagoPedidoReservaEntity convertToEntity(PagoPedidoReservaDTO dto, Class<PagoPedidoReservaEntity> entityClass) {
        return modelMapper.map(dto, PagoPedidoReservaEntity.class);
    }

    public void updateEntityFromDTO(PagoPedidoReservaDTO dto, PagoPedidoReservaEntity entity) {
        modelMapper.map(dto, entity);
    }
}