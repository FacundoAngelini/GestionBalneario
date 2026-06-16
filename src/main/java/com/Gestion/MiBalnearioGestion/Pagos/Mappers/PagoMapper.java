package com.Gestion.MiBalnearioGestion.Pagos.Mappers;


import com.Gestion.MiBalnearioGestion.Common.Configuracion.*;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoDTO;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoResponseDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoLugarEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoMesaEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoReservaEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagoMapper implements IMapper<PagoEntity, PagoDTO> {

    private final ModelMapper modelMapper;

    @Override
    public PagoDTO convertToDTO(PagoEntity entity) {
        return modelMapper.map(entity, PagoDTO.class);
    }

    @Override
    public PagoEntity convertToEntity(PagoDTO dto, Class<PagoEntity> entityClass) {
        return modelMapper.map(dto, PagoEntity.class);
    }

    public void updateEntityFromDTO(PagoDTO dto, PagoEntity entity) { //actualiza la entity con los datos del dto sin encesidad de crear una nueva y sin perder los datps de otros campos
        modelMapper.map(dto, entity);
    }

    public PagoResponseDTO toDTO(PagoEntity pago) {
        PagoResponseDTO.PagoResponseDTOBuilder builder = PagoResponseDTO.builder()
                .publicId(pago.getPublicId())
                .monto(pago.getMonto())
                .estadoPago(pago.getEestadoPago())
                .fechaPago(pago.getFechaPago())
                .metodoPago(pago.getMetodoPago())
                .descuento(pago.getDescuento());

        if (pago instanceof PagoReservaEntity pagoReserva) {
            builder.tipoPago("RESERVA")
                    .reservaPublicId(pagoReserva.getReserva() != null
                            ? pagoReserva.getReserva().getPublicId()
                            : null);

        } else if (pago instanceof PagoPedidoLugarEntity pagoLugar) {
            builder.tipoPago("PEDIDO_LUGAR")
                    .pedidoPublicId(pagoLugar.getPedido() != null
                            ? pagoLugar.getPedido().getPublicId()
                            : null);

        } else if (pago instanceof PagoPedidoMesaEntity pagoMesa) {
            builder.tipoPago("PEDIDO_MESA")
                    .pedidoPublicId(pagoMesa.getPedidoMesa() != null
                            ? pagoMesa.getPedidoMesa().getPublicId()
                            : null);
        }

        return builder.build();
    }
}

