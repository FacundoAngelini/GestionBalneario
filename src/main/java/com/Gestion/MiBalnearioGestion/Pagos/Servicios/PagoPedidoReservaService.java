package com.Gestion.MiBalnearioGestion.Pagos.Servicios;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoReservaDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoReservaEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Mappers.PagoPedidoReservaMapper;
import com.Gestion.MiBalnearioGestion.Pagos.MercadoPagoService;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iPagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagoPedidoReservaService {

    private final iPagoRepository pagoRepository;
    private final PagoPedidoReservaMapper mapper;
    private final MercadoPagoService mercadoPagoService;

    @Transactional
    public String iniciarPago(PagoPedidoReservaDTO dto) {
        PagoPedidoReservaEntity entity = mapper.convertToEntity(dto, PagoPedidoReservaEntity.class);

        entity.setPublicId(UUID.randomUUID());
        entity.setEestadoPago(EestadoPago.PENDIENTE);
        entity.setFechaPago(LocalDate.now());

        entity = pagoRepository.save(entity);

        return mercadoPagoService.crearPreferenciaPago(
                entity.getPublicId(),
                entity.getMonto(),
                "Pedido Gastronómico - Envio a Carpa"
        );
    }
}