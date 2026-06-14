package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Pago;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.EmpleadosRepositorio;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoReservaResponseDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoReservaEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.TicketEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.MetodoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iPagoRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Repository.iTicketRepository;
import com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces.IPagoReservaService;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ReservaRepository;
import com.Gestion.MiBalnearioGestion.Reservas.Servicio.ReservaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class PagoReservaService implements IPagoReservaService {

    private final iPagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final iTicketRepository ticketRepository;
    private final EmpleadosRepositorio empleadoRepository;
    private final ReservaServicio reservaServicio;

    @Transactional
    @Override
    public PagoReservaResponseDTO procesarPagoEfectivoMostrador(ReservaDTO reservaDTO, UUID empleadoPublicId) {
        EmpleadoEntity empleadoCaja = empleadoRepository.findByPublicId(empleadoPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Empleado no identificado en el sistema", "EmpleadoEntity"));

        ReservaEntity reserva = reservaServicio.crearReservaInicial(reservaDTO);

        PagoReservaEntity pagoReserva = PagoReservaEntity.builder()
                .publicId(UUID.randomUUID())
                .monto(reserva.getMontoTotal())
                .eestadoPago(EestadoPago.PAGADO)
                .fechaPago(LocalDate.now())
                .metodoPago(MetodoPago.EFECTIVO)
                .descuento(0.0)
                .reserva(reserva)
                .build();

        pagoRepository.save(pagoReserva);

        reserva.setPagosReservaaa(pagoReserva);
        reserva.setEstadoReserva(EReservaEstado.CONFIRMADA);
        reserva.setReservado(true);
        reservaRepository.save(reserva);

        TicketEntity ticket = TicketEntity.builder()
                .publicId(UUID.randomUUID())
                .fechaTicket(LocalDateTime.now())
                .total(reserva.getMontoTotal())
                .pagoEntity(pagoReserva)
                .empleado(empleadoCaja)
                .build();

        ticketRepository.save(ticket);
        PagoReservaResponseDTO respuesta = new PagoReservaResponseDTO();

        respuesta.setPublicId(pagoReserva.getPublicId());
        respuesta.setMonto(pagoReserva.getMonto());
        respuesta.setEstadoPago(pagoReserva.getEestadoPago());
        respuesta.setFechaPago(pagoReserva.getFechaPago());
        respuesta.setMetodoPago(pagoReserva.getMetodoPago());
        respuesta.setDescuento(pagoReserva.getDescuento());
        respuesta.setReservaPublicId(
                reserva.getPublicId()
        );


        return respuesta;
    }
}