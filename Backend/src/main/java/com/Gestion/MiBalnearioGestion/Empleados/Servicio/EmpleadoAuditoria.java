package com.Gestion.MiBalnearioGestion.Empleados.Servicio;

import com.Gestion.MiBalnearioGestion.Auth.Security.ActorActual;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EventoEmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EAccionEmpleado;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEventoEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.EmpleadoEventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EmpleadoAuditoria {

    private final EmpleadoEventoRepository repositorio;
    private final ActorActual actorActual;

    /** Se llama dentro de la misma transacción de la acción. Quien actúa se toma siempre del token. */
    public void registrar(EmpleadoEntity empleado, EAccionEmpleado accion, String motivo, String detalle) {
        ActorActual.Actor actor = actorActual.obtener();
        repositorio.save(EmpleadoEventoEntity.builder()
                .empleado(empleado)
                .accion(accion)
                .usuario(actor.usuario())
                .actor(actor.empleado())
                .fecha(LocalDateTime.now())
                .motivo(motivo)
                .detalle(detalle)
                .build());
    }

    @Transactional(readOnly = true)
    public List<EventoEmpleadoDTO> historial(UUID empleadoPublicId) {
        return repositorio.findByEmpleadoPublicIdOrderByFechaAscIdAsc(empleadoPublicId).stream()
                .map(e -> new EventoEmpleadoDTO(
                        e.getPublicId(), e.getAccion(), e.getUsuario(),
                        e.getActor() == null ? null : e.getActor().getNombre() + " " + e.getActor().getApellido(),
                        e.getFecha(), e.getMotivo(), e.getDetalle()))
                .toList();
    }
}