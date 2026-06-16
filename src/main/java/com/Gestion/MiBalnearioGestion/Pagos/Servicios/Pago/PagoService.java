package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago;

import com.Gestion.MiBalnearioGestion.Common.Email.EmailService;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.*;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.*;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.MetodoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Mappers.PagoMapper;
import com.Gestion.MiBalnearioGestion.Pagos.Mappers.PagoReservaMapper;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.IPagoRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.ITicketRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IPagoService;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Specification.PagoSpecification;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoLugarEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoMesaEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.Enum.EEstadoPedido;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.IPedidoRepository;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ReservaRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;


import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PagoService implements IPagoService {
    @Value("${mp.accesstoken}")
    private String accessToken;

    private final IPagoRepository pagoRepository;
    private final ITicketRepository ticketRepository;
    private final ReservaRepository reservaRepository;
    private final EmailService emailService;
    private final IPedidoRepository pedidoRepository;
    private final PagoMapper pagoMapper;

    @Transactional(readOnly = true)
    @Override
    public PagoDTO obtenerPagoPorPedido(UUID pedidoPublicId) {
        PagoEntity pago = pagoRepository.findByPedidoPublicId(pedidoPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "No existe pago para el pedido", pedidoPublicId.toString()));

        return PagoDTO.builder()
                .publicId(pago.getPublicId())
                .monto(pago.getMonto())
                .eestadoPago(pago.getEestadoPago())
                .fechaPago(pago.getFechaPago())
                .metodoPago(pago.getMetodoPago())
                .build();
    }

    @Transactional
    @Override
    public synchronized void procesarNotificacionPago(String paymentIdMP) {
        try {
            MercadoPagoConfig.setAccessToken(accessToken);

            PaymentClient client = new PaymentClient();

            Payment payment;
            try {
                payment = client.get(Long.parseLong(paymentIdMP));
            } catch (com.mercadopago.exceptions.MPException | com.mercadopago.exceptions.MPApiException mpEx) {
                System.err.println("Error al consultar el pago en Mercado Pago: " + mpEx.getMessage());
                throw new RuntimeException("Error de comunicación con la API externa de Mercado Pago", mpEx);
            }

            UUID publicIdLocal = UUID.fromString(payment.getExternalReference());

            PagoEntity pagoGeneric = pagoRepository.findByPublicId(publicIdLocal)
                    .orElseThrow(() -> new RuntimeException("No existe el registro de pago local para ID: " + publicIdLocal));

            if (pagoGeneric.getEestadoPago() == EestadoPago.PAGADO) {
                return;
            }

            System.out.println("Estado del pago MP: " + payment.getStatus());

            if ("approved".equals(payment.getStatus())) {

                boolean yaExisteTicket = ticketRepository.existsByPagoEntityId(pagoGeneric.getId());
                if (yaExisteTicket) return;

                pagoGeneric.setEestadoPago(EestadoPago.PAGADO);
                pagoRepository.save(pagoGeneric);

                TicketEntity ticketGuardado = ticketRepository.save(TicketEntity.builder()
                        .publicId(UUID.randomUUID())
                        .fechaTicket(LocalDateTime.now())
                        .total(payment.getTransactionAmount().doubleValue())
                        .pagoEntity(pagoGeneric)
                        .build());


                if (pagoGeneric instanceof PagoReservaEntity pagoReserva) {
                    ReservaEntity reserva = pagoReserva.getReserva();
                    reserva.setEstadoReserva(EReservaEstado.CONFIRMADA);
                    reserva.setReservado(true);
                    reservaRepository.save(reserva);

                    emailService.confirmacionPagoReserva(pagoReserva, ticketGuardado)
                            .thenRun(() -> log.info("Email de confirmación de pago enviado exitosamente a: {}", pagoReserva.getReserva().getCliente().getEmail()))
                            .exceptionally(throwable -> {
                                log.error("Fallo el envío del email de confirmación de pago a: {}", pagoReserva.getReserva().getCliente().getEmail(), throwable);
                                return null;
                            });

                }  else if (pagoGeneric instanceof PagoPedidoLugarEntity pagoPedidoLugar) {
                PedidoLugarEntity pedido = pagoPedidoLugar.getPedido();
                pedido.setEstadoPedido(EEstadoPedido.CONFIRMADO);
                pedidoRepository.save(pedido);

                    emailService.confirmacionPagoPedido(pedido, ticketGuardado)
                            .thenRun(() -> log.info("Email de confirmación de pago enviado exitosamente a: {}", pedido.getCliente().getEmail()))
                            .exceptionally(throwable -> {
                                log.error("Fallo el envío del email de confirmación de pago a: {}", pedido.getCliente().getEmail(), throwable);
                                return null;
                            });

                } else if (pagoGeneric instanceof PagoPedidoMesaEntity pagoPedidoMesa) {

                    PedidoMesaEntity pedidoMesa = pagoPedidoMesa.getPedidoMesa();
                    pedidoMesa.setEstadoPedido(EEstadoPedido.CONFIRMADO);
                    pedidoRepository.save(pedidoMesa);

                }
            }

        } catch (Exception e) {
            System.err.println("Error general procesando la notificación: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public PagoReservaResponseDTO obtenerPagoPorReserva(UUID reservaPublicId) {
        ReservaEntity reserva = reservaRepository.findByPublicId(reservaPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Reserva no encontrada", reservaPublicId.toString()));

        PagoEntity pago = pagoRepository.findByReservaPublicId(reservaPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe pago para la reserva", reservaPublicId.toString()));

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
    public List<PagoResponseDTO> buscarPagosConFiltros(EestadoPago estado,
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
                .map(pagoMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public void cancelarPagoYReserva(UUID reservaPublicId) {
        ReservaEntity reserva = reservaRepository.findByPublicId(reservaPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("No se puede cancelar. La reserva no existe: " + reservaPublicId, "ReservaEntity"));

        if (reserva.getEstadoReserva() == EReservaEstado.CANCELADA) {
            throw new DatosInvalidoException("La reserva ya se encuentra cancelada en el sistema.", "PedidoEntity");
        }
        if (reserva.getEstadoReserva() == EReservaEstado.RECHAZADA) {
            throw new DatosInvalidoException("No se puede cancelar una reserva que ya fue RECHAZADA.", "PedidoEntity");
        }

        reserva.setEstadoReserva(EReservaEstado.CANCELADA);
        reserva.setReservado(false);
        reservaRepository.save(reserva);

        PagoEntity pago = pagoRepository.findByReservaPublicId(reservaPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "No se encontró ningún registro de pago para la reserva: " + reservaPublicId, "PagoEntity"));

        if (pago.getEestadoPago() != EestadoPago.RECHAZADO) {
            pago.setEestadoPago(EestadoPago.RECHAZADO);
            pagoRepository.save(pago);
        }
    }
}