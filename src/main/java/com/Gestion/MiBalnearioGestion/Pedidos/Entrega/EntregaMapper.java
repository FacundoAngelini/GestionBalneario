package com.Gestion.MiBalnearioGestion.Pedidos.Entrega;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.IMapper;
import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoResponse;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoEntity;
import com.Gestion.MiBalnearioGestion.Productos.ProductoDTO;
import com.Gestion.MiBalnearioGestion.Productos.ProductoEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntregaMapper implements IMapper<EntregaEntity, EntregaDTO> {

    private final ModelMapper modelMapper;

    @Override
    public EntregaDTO convertToDTO(EntregaEntity entregaEntity) {
        return modelMapper.map(entregaEntity, EntregaDTO.class);
    }

    @Override
    public EntregaEntity convertToEntity(EntregaDTO entregaDTO, Class<EntregaEntity> entityClass) {
        return modelMapper.map(entregaDTO, entityClass);
    }

    public void updateEntityFromDTO(EntregaDTO dto, EntregaEntity entity) { //actualiza la entity con los datos del dto sin encesidad de crear una nueva y sin perder los datps de otros campos
        modelMapper.map(dto, entity);
    }
}

