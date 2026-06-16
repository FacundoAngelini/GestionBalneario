package com.Gestion.MiBalnearioGestion.Reservas.Tareas;

import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ReservaRepository;
import com.Gestion.MiBalnearioGestion.Reservas.Servicio.IReservaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservaLimpiezaScheduler {

    private final ReservaRepository reservaRepositorio;
    private final IReservaServicio reservaServicio;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void limpiarReservasExpiradas() {
        LocalDateTime limiteTolerancia = LocalDateTime.now().minusMinutes(15);
        List<ReservaEntity> expiradas = reservaRepositorio
                .findByEstadoReservaAndFechaCreacionBefore(EReservaEstado.PENDIENTE, limiteTolerancia);

        if (!expiradas.isEmpty()) {
            System.out.println("Limpieza automatica: Cancelando " + expiradas.size() + " reservas por superar los 15 minutos de tolerancia sin pagar");

            for (ReservaEntity reserva : expiradas) {
                try {
                    reservaServicio.cancelarReservaPorExpiracion(reserva.getPublicId());
                } catch (Exception e) {
                    System.err.println("Error al procesar la expiración de la reserva " + reserva.getPublicId() + ": " + e.getMessage());
                }
            }
        }
    }
}