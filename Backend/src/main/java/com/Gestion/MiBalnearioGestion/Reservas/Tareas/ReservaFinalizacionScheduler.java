package com.Gestion.MiBalnearioGestion.Reservas.Tareas;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EAccionReserva;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ReservaRepository;
import com.Gestion.MiBalnearioGestion.Reservas.Servicio.ReservaAuditoriaServicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservaFinalizacionScheduler {

    private final ReservaRepository reservaRepository;
    private final ReservaAuditoriaServicio auditoria;

    /**
     * Pasa a FINALIZADA las reservas confirmadas cuya fecha de fin ya pasó.
     * Corre un minuto después de arrancar (por si la aplicación estuvo apagada) y luego cada hora.
     */
    @Scheduled(initialDelay = 60_000, fixedDelay = 3_600_000)
    @Transactional
    public void finalizarReservasTerminadas() {
        List<ReservaEntity> terminadas = reservaRepository
                .findByEstadoReservaAndFechaFinBefore(EReservaEstado.CONFIRMADA, LocalDate.now());

        for (ReservaEntity reserva : terminadas) {
            boolean cobrada = reserva.getPagosReservaaa() != null
                    && reserva.getPagosReservaaa().getEestadoPago() == EestadoPago.PAGADO;
            reserva.setEstadoReserva(EReservaEstado.FINALIZADA);
            reservaRepository.save(reserva);
            auditoria.registrar(reserva, EAccionReserva.FINALIZADA, null,
                    cobrada ? "Terminó el período" : "Terminó el período sin cobrarse");
        }
        if (!terminadas.isEmpty()) {
            log.info("Se finalizaron {} reservas", terminadas.size());
        }
    }
}