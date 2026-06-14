package com.Gestion.MiBalnearioGestion.Pedidos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.IMapperDual;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoRequest;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoResponse;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;
import com.Gestion.MiBalnearioGestion.Productos.ProductoDTO;
import com.Gestion.MiBalnearioGestion.Productos.ProductoEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PedidoMapper implements IMapperDual<PedidoEntity, PedidoRequest, PedidoResponse> {

    private final ModelMapper modelMapper;

    @Override
    public PedidoRequest convertToDTO(PedidoEntity pedidoEntity) {
        return modelMapper.map(pedidoEntity, PedidoRequest.class);
    }

    @Override
    public PedidoEntity convertToEntity(PedidoRequest pedidoRequest, Class<PedidoEntity> entityClass) {
        return modelMapper.map(pedidoRequest, entityClass);
    }

    public void updateEntityFromDTO(PedidoRequest dto, PedidoEntity entity) { //actualiza la entity con los datos del dto sin encesidad de crear una nueva y sin perder los datps de otros campos
        modelMapper.map(dto, entity);
    }

    @Override
    public PedidoResponse convertToResponseDTO(PedidoEntity entity) {
        return modelMapper.map(entity, PedidoResponse.class);
    }
}


