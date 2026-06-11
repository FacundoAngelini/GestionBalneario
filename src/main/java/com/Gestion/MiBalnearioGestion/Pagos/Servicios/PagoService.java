package com.Gestion.MiBalnearioGestion.Pagos.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.EmpleadosRepositorio;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.*;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.*;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iPagoRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iTicketRepository;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ReservaRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagoService {
    @Value("${mp.accesstoken}")
    private String accessToken;

    private final iPagoRepository pagoRepository;
    private final iTicketRepository ticketRepository;
    private final ReservaRepository reservaRepository;


    @Transactional
    public void procesarNotificacionPago(String paymentIdMP) {
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
}