package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.AccionInvalidaException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.EmpleadosRepositorio;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.TicketDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.*;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.IPagoRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.ITicketRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IPagoPedidoGastronomicoService;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.*;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.IPedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class PagoPedidoGastronomicoService implements IPagoPedidoGastronomicoService {

    private final IPedidoRepository pedidoRepository;
    private final IPagoRepository pagoRepository;
    private final ITicketRepository ticketRepository;
    private final EmpleadosRepositorio empleadoRepository;

    @Transactional
    @Override
    public TicketDTO procesarPagoPresencial(UUID pedidoPublicId,
                                            UUID empleadoPublicId,
                                            MetodoPago metodo) {

        EmpleadoEntity empleadoCaja = empleadoRepository.findByPublicId(empleadoPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Empleado no identificado", empleadoPublicId.toString()));

        PedidoEntity pedido = pedidoRepository.findByPublicId(pedidoPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Pedido no encontrado", pedidoPublicId.toString()));

        if (pedido.getEstadoPedido() == EEstadoPedido.CONFIRMADO) {
            throw new DatosInvalidoException("El pedido ya está confirmado y pagado.", "PedidoEntity");
        }
        if (pedido.getEstadoPedido() == EEstadoPedido.CANCELADO) {
            throw new AccionInvalidaException("No se puede pagar un pedido cancelado.", "PedidoEntity");
        }

        double montoTotal = pedido.getDetallePedidos().stream()
                .mapToDouble(DetallePedidoEntity::getPrecio)
                .sum();

        PagoEntity pago;
        if (pedido instanceof PedidoLugarEntity pedidoLugar) {
            pago = PagoPedidoLugarEntity.builder()
                    .monto(montoTotal)
                    .eestadoPago(EestadoPago.PAGADO)
                    .fechaPago(LocalDate.now())
                    .metodoPago(metodo)
                    .descuento(0.0)
                    .pedido(pedidoLugar)
                    .build();
        } else {
            pago = PagoPedidoMesaEntity.builder()
                    .monto(montoTotal)
                    .eestadoPago(EestadoPago.PAGADO)
                    .fechaPago(LocalDate.now())
                    .metodoPago(metodo)
                    .descuento(0.0)
                    .pedidoMesa((PedidoMesaEntity) pedido)
                    .build();
        }

        pago = pagoRepository.save(pago);
        pagoRepository.flush();

        pedido.setEstadoPedido(EEstadoPedido.CONFIRMADO);
        pedidoRepository.save(pedido);

        TicketEntity ticket = ticketRepository.save(TicketEntity.builder()
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
                .pedidoPublicId(pedidoPublicId)
                .build();
    }
}