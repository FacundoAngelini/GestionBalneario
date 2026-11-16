package com.Gestion.MiBalnearioGestion.Clientes.Service;

import com.Gestion.MiBalnearioGestion.Auth.Security.ActorActual;
import com.Gestion.MiBalnearioGestion.Clientes.Entity.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.Entity.ClienteEventoEntity;
import com.Gestion.MiBalnearioGestion.Clientes.Entity.EAccionCliente;
import com.Gestion.MiBalnearioGestion.Clientes.Repository.ClienteEventoRepository;
import com.Gestion.MiBalnearioGestion.Clientes.dto.EventoClienteDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClienteAuditoria {

    private final ClienteEventoRepository repositorio;
    private final ActorActual actorActual;

    /** Quien actúa se toma siempre del token. Se llama dentro de la misma transacción de la acción. */
    public void registrar(ClienteEntity cliente, EAccionCliente accion, String motivo, String detalle) {
        ActorActual.Actor actor = actorActual.obtener();
        guardar(cliente, accion, actor.usuario(), actor.empleado(), motivo, detalle);
    }

    /** Para el registro propio: todavía no hay sesión, así que el usuario se informa explícitamente. */
    public void registrarComo(ClienteEntity cliente, EAccionCliente accion, String usuario, String motivo, String detalle) {
        guardar(cliente, accion, usuario, null, motivo, detalle);
    }

    private void guardar(ClienteEntity cliente, EAccionCliente accion, String usuario,
                         EmpleadoEntity actor, String motivo, String detalle) {
        repositorio.save(ClienteEventoEntity.builder()
                .cliente(cliente).accion(accion).usuario(usuario).actor(actor)
                .fecha(LocalDateTime.now()).motivo(motivo).detalle(detalle).build());
    }

    @Transactional(readOnly = true)
    public List<EventoClienteDTO> historial(UUID clientePublicId) {
        return repositorio.findByClientePublicIdOrderByFechaAscIdAsc(clientePublicId).stream()
                .map(e -> new EventoClienteDTO(
                        e.getPublicId(), e.getAccion(), e.getUsuario(),
                        e.getActor() == null ? null : e.getActor().getNombre() + " " + e.getActor().getApellido(),
                        e.getFecha(), e.getMotivo(), e.getDetalle()))
                .toList();
    }
}