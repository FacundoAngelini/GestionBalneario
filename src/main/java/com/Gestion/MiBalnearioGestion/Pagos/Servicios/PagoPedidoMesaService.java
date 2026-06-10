package com.Gestion.MiBalnearioGestion.Pagos.Servicios;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoMesaDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoMesaEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Mappers.PagoPedidoMesaMapper;
import com.Gestion.MiBalnearioGestion.Pagos.MercadoPagoService;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iPagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagoPedidoMesaService {

    private final iPagoRepository pagoRepository;
    private final PagoPedidoMesaMapper mapper;
    private final MercadoPagoService mercadoPagoService;

    @Transactional
    public String iniciarPago(PagoPedidoMesaDTO dto) {
        PagoPedidoMesaEntity entity = mapper.convertToEntity(dto, PagoPedidoMesaEntity.class);

        entity.setPublicId(UUID.randomUUID());
        entity.setEestadoPago(EestadoPago.PENDIENTE);
        entity.setFechaPago(LocalDate.now());

        entity = pagoRepository.save(entity);

        return mercadoPagoService.crearPreferenciaPago(
                entity.getPublicId(),
                entity.getMonto(),
                "Pedido Gastronómico - Mesa"
        );
    }
}