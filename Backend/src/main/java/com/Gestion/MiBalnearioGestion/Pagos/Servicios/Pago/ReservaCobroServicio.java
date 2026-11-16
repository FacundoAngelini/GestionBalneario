package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago;

import com.Gestion.MiBalnearioGestion.Auth.Security.ActorActual;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.CobroComprobanteDTO;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.ReservaPresencialDTO;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.ReservaPresencialResponseDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoReservaEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.IPagoRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.ITicketRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IReservaCobroServicio;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EAccionReserva;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ReservaRepository;
import com.Gestion.MiBalnearioGestion.Reservas.Servicio.IReservaServicio;
import com.Gestion.MiBalnearioGestion.Reservas.Servicio.ReservaAuditoriaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservaCobroServicio implements IReservaCobroServicio {

    // También se puede cobrar una reserva terminada que quedó sin cobrar
    private static final List<EReservaEstado> COBRABLES =
            List.of(EReservaEstado.PENDIENTE, EReservaEstado.CONFIRMADA, EReservaEstado.FINALIZADA);

    private final ReservaRepository reservaRepository;
    private final IPagoRepository pagoRepository;
    private final ITicketRepository ticketRepository;
    private final IReservaServicio reservaServicio;
    private final ReservaAuditoriaServicio auditoria;
    private final ActorActual actorActual;
    private final ConfiguracionPagoService configuracionPago;

    @Transactional
    @Override
    public CobroComprobanteDTO registrarCobro(UUID reservaPublicId, MetodoPago metodo) {
        return cobrar(buscar(reservaPublicId), metodo);
    }

    @Transactional(readOnly = true)
    @Override
    public CobroComprobanteDTO obtenerComprobante(UUID reservaPublicId) {
        ReservaEntity reserva = buscar(reservaPublicId);
        PagoReservaEntity pago = reserva.getPagosReservaaa();
        if (pago == null || pago.getEestadoPago() != EestadoPago.PAGADO) {
            throw new EntidadNoEncontradaException("Esta reserva no tiene un cobro vigente", reservaPublicId.toString());
        }
        TicketEntity ticket = ticketRepository.findByPagoEntityId(pago.getId())
                .orElseThrow(() -> new EntidadNoEncontradaException("No hay comprobante para este cobro", reservaPublicId.toString()));
        return armarComprobante(reserva, pago, ticket);
    }

    @Transactional
    @Override
    public ReservaDTO anularCobro(UUID reservaPublicId, String motivo) {
        ReservaEntity reserva = buscar(reservaPublicId);
        PagoReservaEntity pago = reserva.getPagosReservaaa();
        if (pago == null || pago.getEestadoPago() != EestadoPago.PAGADO) {
            throw new DatosInvalidoException("Esta reserva no tiene un cobro vigente para anular", "PagoReservaEntity");
        }
        pago.setEestadoPago(EestadoPago.ANULADO);
        pagoRepository.save(pago);
        // La reserva sigue como está (confirmada), pero ahora figura sin cobrar
        auditoria.registrar(reserva, EAccionReserva.COBRO_ANULADO, motivo.trim(),
                "Se anuló el cobro de $" + pago.getMonto());
        return reservaServicio.buscarPorPublicId(reservaPublicId);
    }

    @Transactional
    @Override
    public ReservaPresencialResponseDTO crearPresencial(ReservaPresencialDTO dto) {
        ReservaEntity reserva = reservaServicio.crearReservaInicial(ReservaDTO.builder()
                .clientePublicId(dto.clientePublicId())
                .fechaInicio(dto.fechaInicio())
                .fechaFin(dto.fechaFin())
                .modalidad(dto.modalidad())
                .recursosPublicIds(dto.recursosPublicIds())
                .build());

        CobroComprobanteDTO comprobante = null;
        if (dto.cobrarAhora()) {
            comprobante = cobrar(reserva, MetodoPago.EFECTIVO);
        } else {
            // Quien la carga en el mostrador la confirma en el momento; el cobro queda para después
            reserva.setEstadoReserva(EReservaEstado.CONFIRMADA);
            reserva.setReservado(true);
            reservaRepository.save(reserva);
            auditoria.registrar(reserva, EAccionReserva.CONFIRMADA, null, "Reserva presencial confirmada sin cobro");
        }
        return new ReservaPresencialResponseDTO(reservaServicio.buscarPorPublicId(reserva.getPublicId()), comprobante);
    }

    // ---- Interno ----

    private CobroComprobanteDTO cobrar(ReservaEntity reserva, MetodoPago metodo) {
        if (metodo != MetodoPago.EFECTIVO) {
            throw new DatosInvalidoException("Por ahora solo se puede cobrar en efectivo", "PagoReservaEntity");
        }
        if (!COBRABLES.contains(reserva.getEstadoReserva())) {
            throw new DatosInvalidoException("No se puede cobrar una reserva en estado " + reserva.getEstadoReserva(), "ReservaEntity");
        }
        PagoReservaEntity existente = reserva.getPagosReservaaa();
        if (existente != null && existente.getEestadoPago() == EestadoPago.PAGADO) {
            throw new DatosInvalidoException("Esta reserva ya está cobrada", "PagoReservaEntity");
        }

        ActorActual.Actor actor = actorActual.obtener();
        double descuento = configuracionPago.obtener().getDescuentoEfectivo();
        double monto = Math.round(reserva.getMontoTotal() * (1 - descuento / 100.0) * 100.0) / 100.0;
        LocalDateTime ahora = LocalDateTime.now();

        // Una reserva tiene un solo registro de pago: si hubo un cobro anulado antes, se vuelve a usar
        PagoReservaEntity pago = existente != null
                ? existente
                : PagoReservaEntity.builder().publicId(UUID.randomUUID()).reserva(reserva).build();
        pago.setMonto(monto);
        pago.setEestadoPago(EestadoPago.PAGADO);
        pago.setFechaPago(ahora.toLocalDate());
        pago.setMetodoPago(metodo);
        pago.setDescuento(descuento);
        pago.setCobradoPor(actor.usuario());
        pago.setFechaHoraCobro(ahora);
        PagoReservaEntity guardado = pagoRepository.save(pago);
        reserva.setPagosReservaaa(guardado);

        // Un cobro confirma la reserva si estaba pendiente
        boolean seConfirma = reserva.getEstadoReserva() == EReservaEstado.PENDIENTE;
        if (seConfirma) reserva.setEstadoReserva(EReservaEstado.CONFIRMADA);
        reserva.setReservado(true);
        reservaRepository.save(reserva);

        // Un pago tiene un solo ticket: si ya existía (cobro anulado y vuelto a cobrar), se actualiza
        TicketEntity ticket = ticketRepository.findByPagoEntityId(guardado.getId())
                .orElseGet(() -> TicketEntity.builder().publicId(UUID.randomUUID()).pagoEntity(guardado).build());
        ticket.setFechaTicket(ahora);
        ticket.setTotal(monto);
        ticket.setEmpleado(actor.empleado());
        ticketRepository.save(ticket);

        if (seConfirma) {
            auditoria.registrar(reserva, EAccionReserva.CONFIRMADA, null, "Confirmada al registrar el cobro");
        }
        auditoria.registrar(reserva, EAccionReserva.COBRO_REGISTRADO, null,
                "Efectivo: $" + monto + (descuento > 0 ? " (con " + descuento + " % de descuento)" : ""));
        return armarComprobante(reserva, guardado, ticket);
    }

    private CobroComprobanteDTO armarComprobante(ReservaEntity reserva, PagoReservaEntity pago, TicketEntity ticket) {
        ReservaDTO r = reservaServicio.buscarPorPublicId(reserva.getPublicId());
        double subtotal = reserva.getMontoTotal();
        double descuentoMonto = Math.round((subtotal - pago.getMonto()) * 100.0) / 100.0;
        String empleado = ticket.getEmpleado() == null
                ? null
                : ticket.getEmpleado().getNombre() + " " + ticket.getEmpleado().getApellido();
        return new CobroComprobanteDTO(
                ticket.getPublicId(), reserva.getPublicId(), ticket.getFechaTicket(),
                r.getClienteNombre(), r.getClienteDni(), r.getRecursosDetalle(),
                reserva.getFechaInicio(), reserva.getFechaFin(), reserva.getModalidad(),
                subtotal, pago.getDescuento(), descuentoMonto, pago.getMonto(),
                pago.getMetodoPago(), pago.getCobradoPor(), empleado);
    }

    private ReservaEntity buscar(UUID publicId) {
        return reservaRepository.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("La reserva no existe", publicId.toString()));
    }
}