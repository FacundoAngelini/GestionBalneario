package com.Gestion.MiBalnearioGestion.Pagos.Mappers;

import com.Gestion.MiBalnearioGestion.Common.Configuracion.*;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoDTO;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoMesaDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoMesaEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagoPedidoMesaMapper implements IMapper<PagoPedidoMesaEntity, PagoPedidoMesaDTO> {

    private final ModelMapper modelMapper;

    @Override
    public PagoPedidoMesaDTO convertToDTO(PagoPedidoMesaEntity entity) {
        return modelMapper.map(entity, PagoPedidoMesaDTO.class);
    }

    @Override
    public PagoPedidoMesaEntity convertToEntity(PagoPedidoMesaDTO dto, Class<PagoPedidoMesaEntity> entityClass) {
        return modelMapper.map(dto, PagoPedidoMesaEntity.class);
    }

    public void updateEntityFromDTO(PagoPedidoMesaDTO dto, PagoPedidoMesaEntity entity) {
        modelMapper.map(dto, entity);
    }
}