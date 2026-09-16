package com.Gestion.MiBalnearioGestion.Pedidos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.IMapperDual;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.DetallePedidoRequest;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.DetallePedidoResponse;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoRequest;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoResponse;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.DetallePedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DetallePedidoMapper implements IMapperDual <DetallePedidoEntity, DetallePedidoRequest, DetallePedidoResponse>{

    private final ModelMapper modelMapper;

    @Override
    public DetallePedidoRequest convertToDTO(DetallePedidoEntity detallePedidoEntity) {
        return modelMapper.map(detallePedidoEntity, DetallePedidoRequest.class);
    }

    @Override
    public DetallePedidoEntity convertToEntity(DetallePedidoRequest detallePedidoRequest, Class<DetallePedidoEntity> entityClass) {
        return modelMapper.map(detallePedidoRequest, entityClass);
    }

    public void updateEntityFromDTO(DetallePedidoRequest dto, DetallePedidoEntity entity) {
        modelMapper.map(dto, entity);
    }

    @Override
    public DetallePedidoResponse convertToResponseDTO(DetallePedidoEntity entity) {
        return modelMapper.map(entity, DetallePedidoResponse.class);
    }
}
