package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.*;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.*;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.MetodoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Mappers.PagoReservaMapper;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iPagoRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iTicketRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IPagoService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Specification.PagoSpecification;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ReservaRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagoService implements IPagoService {
    @Value("${mp.accesstoken}")
    private String accessToken;

    private final iPagoRepository pagoRepository;
    private final iTicketRepository ticketRepository;
    private final ReservaRepository reservaRepository;
    private final PagoReservaMapper pagoReservaMapper;


    @Transactional
    @Override
    public synchronized void procesarNotificacionPago(String paymentIdMP) {
        try {
            MercadoPagoConfig.setAccessToken(accessToken);

            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.parseLong(paymentIdMP));

            UUID publicIdLocal = UUID.fromString(payment.getExternalReference());

            PagoEntity pagoGeneric = pagoRepository.findByPublicId(publicIdLocal)
                    .orElseThrow(() -> new RuntimeException("No existe el registro de pago local para ID: " + publicIdLocal));

            if (pagoGeneric.getEestadoPago() == EestadoPago.PAGADO) {
                return;
            }

            System.out.println(payment.getStatus());

            if ("approved".equals(payment.getStatus())) {

                boolean yaExisteTicket = ticketRepository.existsByPagoEntityId(pagoGeneric.getId());

                if (yaExisteTicket) {
                    System.out.println("La notificación ya fue procesada anteriormente. Evitando duplicados.");
                    return; // Corta la ejecución acá, no hace inserts repetidos ni rompe por deadlock
                }
                // ========================================================

                System.out.println("ENTRO IF DE PAGO APPROVED");
                pagoGeneric.setEestadoPago(EestadoPago.PAGADO);
                pagoRepository.save(pagoGeneric);

                if (pagoGeneric instanceof PagoReservaEntity pagoReserva) {
                    ReservaEntity reserva = pagoReserva.getReserva();
                    reserva.setEstadoReserva(EReservaEstado.CONFIRMADA);
                    reserva.setReservado(true);
                    reservaRepository.save(reserva);
                }

                TicketEntity ticket = TicketEntity.builder()
                        .publicId(UUID.randomUUID())
                        .fechaTicket(LocalDateTime.now())
                        .total(payment.getTransactionAmount().doubleValue())
                        .pagoEntity(pagoGeneric)
                        //.empleado(empleadoSistema)
                        .build();

                ticketRepository.save(ticket);

            } else if ("rejected".equals(payment.getStatus())) {
                pagoGeneric.setEestadoPago(EestadoPago.RECHAZADO);
                pagoRepository.save(pagoGeneric);

                if (pagoGeneric instanceof PagoReservaEntity pagoReserva) {
                    ReservaEntity reserva = pagoReserva.getReserva();
                    reserva.setEstadoReserva(EReservaEstado.RECHAZADA);
                    reservaRepository.save(reserva);
                }
            }

        } catch (MPException | MPApiException e) {
            throw new RuntimeException("Fallo critico al validar la operación con la API de Mercado Pago", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public PagoReservaResponseDTO obtenerPagoPorReserva(UUID reservaPublicId) {
        ReservaEntity reserva = reservaRepository.findByPublicId(reservaPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Reserva no encontrada", reservaPublicId.toString()));

        PagoEntity pago = pagoRepository.findByReservaPublicId(reservaPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "No existe pago para la reserva", reservaPublicId.toString()));

        return PagoReservaResponseDTO.builder()
                .publicId(pago.getPublicId())
                .monto(pago.getMonto())
                .estadoPago(pago.getEestadoPago())
                .fechaPago(pago.getFechaPago())
                .metodoPago(pago.getMetodoPago())
                .descuento(pago.getDescuento())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<PagoReservaResponseDTO> buscarPagosConFiltros(EestadoPago estado,
                                                              MetodoPago metodo,
                                                              Double montoMin,
                                                              Double montoMax,
                                                              LocalDate fechaDesde,
                                                              LocalDate fechaHasta) {
        PredicateSpecification<PagoEntity> spec =
                PredicateSpecification.allOf(
                        PagoSpecification.estadoIgual(estado),
                        PagoSpecification.metodoPagoIgual(metodo),
                        PagoSpecification.montoMenorOIgual(montoMax),
                        PagoSpecification.montoMayorOIgual(montoMin),
                        PagoSpecification.fechaPagoDesde(fechaDesde),
                        PagoSpecification.fechaPagoHasta(fechaHasta)
                );

        return pagoRepository.findAll(spec)
                .stream()
                .filter(pago -> pago instanceof PagoReservaEntity)
                .map(pago -> (PagoReservaEntity) pago)
                .map(pagoReserva -> PagoReservaResponseDTO.builder()
                        .publicId(pagoReserva.getPublicId())
                        .reservaPublicId(pagoReserva.getReserva() != null ? pagoReserva.getReserva().getPublicId() : null)
                        .monto(pagoReserva.getMonto())
                        .estadoPago(pagoReserva.getEestadoPago())
                        .fechaPago(pagoReserva.getFechaPago())
                        .metodoPago(pagoReserva.getMetodoPago())
                        .descuento(pagoReserva.getDescuento())
                        .build())
                .toList();
    }

    @Transactional
    @Override
    public void cancelarPagoYReserva(UUID reservaPublicId) {
        ReservaEntity reserva = reservaRepository.findByPublicId(reservaPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("No se puede cancelar. La reserva no existe"+ reservaPublicId.toString(), "ReservaEntity"));

        if (reserva.getEstadoReserva() == EReservaEstado.CANCELADA) {
            throw new IllegalStateException("La reserva ya se encuentra cancelada en el sistema");
        }
        if (reserva.getEstadoReserva() == EReservaEstado.RECHAZADA) {
            throw new IllegalStateException("No se puede cancelar una reserva que ya fue RECHAZADA");
        }

        reserva.setEstadoReserva(EReservaEstado.CANCELADA);
        reserva.setReservado(false);
        reservaRepository.save(reserva);


        PagoEntity pago = pagoRepository.findByReservaPublicId(reservaPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontro ningun registro de pago para la reserva especificada."+ reservaPublicId.toString(),"PagoEntity"));

        if (pago.getEestadoPago() != EestadoPago.RECHAZADO) {
            pago.setEestadoPago(EestadoPago.RECHAZADO);
            pagoRepository.save(pago);
        }
    }
}