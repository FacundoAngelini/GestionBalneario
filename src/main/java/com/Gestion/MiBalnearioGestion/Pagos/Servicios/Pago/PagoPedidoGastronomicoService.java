package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago;

import com.Gestion.MiBalnearioGestion.Common.Email.EmailService;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.EmpleadosRepositorio;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.TicketDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.*;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.MetodoPago;
import com.Gestion.MiBalnearioGestion.Pagos.MercadoPagoService;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iPagoRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iTicketRepository;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.*;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.iPedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class PagoPedidoGastronomicoService {

    private final iPedidoRepository pedidoRepository;
    private final iPagoRepository pagoRepository;
    private final iTicketRepository ticketRepository;
    private final EmpleadosRepositorio empleadoRepository;

    @Transactional
    public TicketDTO procesarPagoPresencial(UUID pedidoPublicId,
                                            UUID empleadoPublicId,
                                            MetodoPago metodo) {

        EmpleadoEntity empleadoCaja = empleadoRepository.findByPublicId(empleadoPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Empleado no identificado", empleadoPublicId.toString()));

        PedidoEntity pedido = pedidoRepository.findByPublicId(pedidoPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Pedido no encontrado", pedidoPublicId.toString()));

        if (pedido.getEstadoPedido() == EEstadoPedido.CONFIRMADO) {
            throw new IllegalStateException("El pedido ya está confirmado y pagado.");
        }
        if (pedido.getEstadoPedido() == EEstadoPedido.CANCELADO) {
            throw new IllegalStateException("No se puede pagar un pedido cancelado.");
        }

        double montoTotal = pedido.getDetallePedidos().stream()
                .mapToDouble(DetallePedidoEntity::getPrecio)
                .sum();

        PagoEntity pago;

        if (pedido instanceof PedidoLugarEntity pedidoLugar) {
            pago = PagoPedidoLugarEntity.builder()
                    .publicId(UUID.randomUUID())
                    .monto(montoTotal)
                    .eestadoPago(EestadoPago.PAGADO)
                    .fechaPago(LocalDate.now())
                    .metodoPago(metodo)
                    .pedido(pedidoLugar)
                    .build();
        } else {
            pago = PagoPedidoMesaEntity.builder()
                    .publicId(UUID.randomUUID())
                    .monto(montoTotal)
                    .eestadoPago(EestadoPago.PAGADO)
                    .fechaPago(LocalDate.now())
                    .metodoPago(metodo)
                    .pedidoMesa((PedidoMesaEntity) pedido)
                    .build();
        }

        pagoRepository.save(pago);

        pedido.setEstadoPedido(EEstadoPedido.CONFIRMADO);
        pedidoRepository.save(pedido);

        TicketEntity ticket = ticketRepository.save(TicketEntity.builder()
                .publicId(UUID.randomUUID())
                .fechaTicket(LocalDateTime.now())
                .total(montoTotal)
                .pagoEntity(pago)
                .empleado(empleadoCaja)
                .build());

        return TicketDTO.builder()
                .publicId(ticket.getPublicId())
                .fechaTicket(ticket.getFechaTicket())
                .total(ticket.getTotal())
                .empleadoPublicId(empleadoCaja.getPublicId())
                .pagoPublicId(pago.getPublicId())
                .build();
    }
}