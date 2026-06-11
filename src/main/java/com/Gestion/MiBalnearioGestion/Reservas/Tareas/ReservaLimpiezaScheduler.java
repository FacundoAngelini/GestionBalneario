package com.Gestion.MiBalnearioGestion.Reservas.Tareas;

import com.Gestion.MiBalnearioGestion.Pagos.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservaLimpiezaScheduler {

    private final ReservaRepository reservaRepositorio;

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void limpiarReservasExpiradas() {
        LocalDate limiteCancelacion = LocalDate.now().plusDays(2);
        List<ReservaEntity> expiradas = reservaRepositorio
                .findReservasExpiradas(EReservaEstado.PENDIENTE, limiteCancelacion);

        if (!expiradas.isEmpty()) {
            expiradas.forEach(r -> {
                r.setEstadoReserva(EReservaEstado.CANCELADA);
                r.setReservado(false);
                if (r.getPagosReservaaa() != null) {
                    r.getPagosReservaaa().setEestadoPago(EestadoPago.RECHAZADO);
                }
            });
            reservaRepositorio.saveAll(expiradas);
            System.out.println("Limpieza automática: " + expiradas.size() + " reservas expiradas canceladas.");
        }
    }
}