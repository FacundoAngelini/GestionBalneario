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
        // 1. Buscamos TODAS las reservas pendientes que tengan más de 15 minutos
        LocalDateTime limiteTolerancia = LocalDateTime.now().minusMinutes(15);
        List<ReservaEntity> expiradas = reservaRepositorio
                .findByEstadoReservaAndFechaCreacionBefore(EReservaEstado.PENDIENTE, limiteTolerancia);

        if (!expiradas.isEmpty()) {
            for (ReservaEntity reserva : expiradas) {

                boolean esManual = reserva.getPagosReservaaa() == null;

                // 2. REGLA DE 48 HS PARA RESERVAS MANUALES
                if (esManual && reserva.getFechaCreacion().isAfter(LocalDateTime.now().minusHours(48))) {
                    continue; // Todavía está en el periodo de gracia de 48hs. Se saltea.
                }

                // 3. REGLA DE 15 MIN (Mercado Pago) o MANUAL VENCIDA (> 48hs)
                try {
                    reservaServicio.cancelarReservaPorExpiracion(reserva.getPublicId());
                    System.out.println("Limpieza automática: Reserva " + reserva.getPublicId() + " CANCELADA por vencimiento.");
                } catch (Exception e) {
                    System.err.println("Error al procesar la expiración de la reserva " + reserva.getPublicId() + ": " + e.getMessage());
                }
            }
        }
    }
}
