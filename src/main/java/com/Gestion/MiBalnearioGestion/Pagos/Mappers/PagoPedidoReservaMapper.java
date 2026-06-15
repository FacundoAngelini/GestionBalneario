package com.Gestion.MiBalnearioGestion.Pagos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.*;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoReservaDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoLugarEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagoPedidoReservaMapper implements IMapper<PagoPedidoLugarEntity, PagoPedidoReservaDTO> {

    private final ModelMapper modelMapper;

    @Override
    public PagoPedidoReservaDTO convertToDTO(PagoPedidoLugarEntity entity) {
        return modelMapper.map(entity, PagoPedidoReservaDTO.class);
    }

    @Override
    public PagoPedidoLugarEntity convertToEntity(PagoPedidoReservaDTO dto, Class<PagoPedidoLugarEntity> entityClass) {
        return modelMapper.map(dto, PagoPedidoLugarEntity.class);
    }

    public void updateEntityFromDTO(PagoPedidoReservaDTO dto, PagoPedidoLugarEntity entity) {
        modelMapper.map(dto, entity);
    }
}