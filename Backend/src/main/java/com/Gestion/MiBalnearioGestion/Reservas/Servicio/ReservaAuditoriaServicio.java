package com.Gestion.MiBalnearioGestion.Reservas.Servicio;

import com.Gestion.MiBalnearioGestion.Auth.Security.ActorActual;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.EventoReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EAccionReserva;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEventoEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ReservaEventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservaAuditoriaServicio {

    private final ReservaEventoRepository repositorio;
    private final ActorActual actorActual;

    /** Se llama dentro de la misma transacción de la acción: si la acción falla, el registro tampoco queda. */
    public void registrar(ReservaEntity reserva, EAccionReserva accion, String motivo, String detalle) {
        ActorActual.Actor actor = actorActual.obtener();
        repositorio.save(ReservaEventoEntity.builder()
                .reserva(reserva)
                .accion(accion)
                .usuario(actor.usuario())
                .empleado(actor.empleado())
                .fecha(LocalDateTime.now())
                .motivo(motivo)
                .detalle(detalle)
                .build());
    }

    @Transactional(readOnly = true)
    public List<EventoReservaDTO> historial(UUID reservaPublicId) {
        return repositorio.findByReservaPublicIdOrderByFechaAscIdAsc(reservaPublicId).stream()
                .map(e -> new EventoReservaDTO(
                        e.getPublicId(),
                        e.getAccion(),
                        e.getUsuario(),
                        e.getEmpleado() == null ? null : e.getEmpleado().getNombre() + " " + e.getEmpleado().getApellido(),
                        e.getFecha(),
                        e.getMotivo(),
                        e.getDetalle()))
                .toList();
    }
}