package com.Gestion.MiBalnearioGestion.Clientes.Service;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Entity.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.CredencialRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.JWT.JwtService;
import com.Gestion.MiBalnearioGestion.Auth.Roles.Repositorio.RolesRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.Roles.Roles;
import com.Gestion.MiBalnearioGestion.Auth.Roles.RolesEntity;
import com.Gestion.MiBalnearioGestion.Clientes.Entity.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.Entity.EAccionCliente;
import com.Gestion.MiBalnearioGestion.Clientes.Mapper.ClienteMapper;
import com.Gestion.MiBalnearioGestion.Clientes.Repository.ClientesRepository;
import com.Gestion.MiBalnearioGestion.Clientes.dto.*;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.AccionInvalidaException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ReservaRepository;
import com.Gestion.MiBalnearioGestion.Usuarios.Entity.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.Exception.CuentaEncontradaException;
import com.Gestion.MiBalnearioGestion.Usuarios.Repository.UsuarioRepository;
import com.Gestion.MiBalnearioGestion.Usuarios.Service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

/** Gestión de clientes por parte del personal, y baja propia del cliente. */
@Service
@RequiredArgsConstructor
public class ClienteGestionService {

    // Un cliente con reservas en alguno de estos estados no puede darse de baja
    private static final List<EReservaEstado> ESTADOS_ACTIVOS = List.of(EReservaEstado.PENDIENTE, EReservaEstado.CONFIRMADA);
    private static final int MAX_RESULTADOS = 200;
    private static final int ULTIMAS_ANTERIORES = 5;

    private final ClientesRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CredencialRepositorio credencialRepositorio;
    private final RolesRepositorio rolesRepositorio;
    private final ReservaRepository reservaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final IUsuarioService usuarioService;
    private final ClienteMapper clienteMapper;
    private final ClienteAuditoria auditoria;

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar(String texto, Boolean activo) {
        List<PredicateSpecification<ClienteEntity>> specs = new ArrayList<>();
        specs.add(ClienteSpecification.textoContiene(texto));
        if (activo != null) specs.add(ClienteSpecification.estadoIgual(activo));

        return clienteRepository.findAll(PredicateSpecification.allOf(specs)).stream()
                .sorted(Comparator.comparing(ClienteEntity::getApellido, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(ClienteEntity::getNombre, String.CASE_INSENSITIVE_ORDER))
                .limit(MAX_RESULTADOS)
                .map(clienteMapper::convertToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteFichaDTO ficha(UUID id) {
        return aFicha(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<EventoClienteDTO> historial(UUID id) {
        buscar(id);
        return auditoria.historial(id);
    }

    @Transactional
    public ClienteFichaDTO crear(ClienteAltaDTO dto) {
        String nombreUsuario = dto.credencial().nombreUsuario().trim();
        if (credencialRepositorio.existsByNombreUsuario(nombreUsuario)) {
            throw new CuentaEncontradaException("Ya existe ese nombre de usuario", "CredencialEntity");
        }
        validarDatosUnicos(dto.dni(), dto.email().trim(), dto.telefono().trim(), null);

        RolesEntity rolCliente = rolesRepositorio.findByRole(Roles.ROLE_CLIENTE)
                .orElseThrow(() -> new DatosInvalidoException("El rol ROLE_CLIENTE no existe en la base de datos", "ClienteEntity"));

        UsuarioEntity usuario = new UsuarioEntity();
        CredencialEntity credencial = CredencialEntity.builder()
                .nombreUsuario(nombreUsuario)
                .contrasenia(passwordEncoder.encode(dto.credencial().contrasenia()))
                .enabled(true)
                .usuario(usuario)
                .roles(Set.of(rolCliente))
                .build();
        credencial.setRefreshToken(jwtService.generateRefreshToken(credencial));

        ClienteEntity cliente = ClienteEntity.builder()
                .nombre(dto.nombre().trim())
                .apellido(dto.apellido().trim())
                .dni(dto.dni())
                .email(dto.email().trim())
                .telefono(dto.telefono().trim())
                .fechaAlta(LocalDate.now())
                .estado(true)
                .usuario(usuario)
                .build();

        usuario.setCredencial(credencial);
        usuario.setCliente(cliente);
        usuarioRepository.save(usuario);
        credencialRepositorio.save(credencial);
        ClienteEntity guardado = clienteRepository.save(cliente);

        auditoria.registrar(guardado, EAccionCliente.ALTA, null, "Alta en el mostrador");
        return aFicha(guardado);
    }

    @Transactional
    public ClienteFichaDTO actualizar(UUID id, ClienteEdicionDTO dto) {
        ClienteEntity cliente = buscar(id);
        validarDatosUnicos(dto.dni(), dto.email().trim(), dto.telefono().trim(), id);

        // Solo se anotan los nombres de los campos modificados, no sus valores
        List<String> cambios = new ArrayList<>();
        if (!Objects.equals(cliente.getNombre(), dto.nombre().trim())) cambios.add("nombre");
        if (!Objects.equals(cliente.getApellido(), dto.apellido().trim())) cambios.add("apellido");
        if (cliente.getDni() != dto.dni()) cambios.add("DNI");
        if (!Objects.equals(cliente.getEmail(), dto.email().trim())) cambios.add("email");
        if (!Objects.equals(cliente.getTelefono(), dto.telefono().trim())) cambios.add("teléfono");

        cliente.setNombre(dto.nombre().trim());
        cliente.setApellido(dto.apellido().trim());
        cliente.setDni(dto.dni());
        cliente.setEmail(dto.email().trim());
        cliente.setTelefono(dto.telefono().trim());
        ClienteEntity guardado = clienteRepository.save(cliente);

        if (!cambios.isEmpty()) {
            auditoria.registrar(guardado, EAccionCliente.DATOS_MODIFICADOS, null, "Se modificó: " + String.join(", ", cambios));
        }
        return aFicha(guardado);
    }

    @Transactional
    public ClienteFichaDTO cambiarEstado(UUID id, CambioEstadoClienteDTO dto) {
        ClienteEntity cliente = buscar(id);
        String motivo = dto.motivo().trim();

        if (dto.activo()) {
            if (cliente.isEstado()) {
                throw new DatosInvalidoException("El cliente ya está activo", "ClienteEntity");
            }
            cliente.setEstado(true);
            clienteRepository.save(cliente);
            usuarioService.reactivarCuenta(cliente.getUsuario());
            auditoria.registrar(cliente, EAccionCliente.REACTIVADO, motivo, "Cuenta reactivada por el personal");
        } else {
            darDeBaja(cliente, motivo, false);
        }
        return aFicha(cliente);
    }

    /** Baja pedida por el propio cliente desde "Mi perfil". */
    @Transactional
    public void darseDeBaja(String nombreUsuario, String motivo) {
        ClienteEntity cliente = credencialRepositorio.findByNombreUsuario(nombreUsuario)
                .map(CredencialEntity::getUsuario)
                .map(UsuarioEntity::getCliente)
                .orElseThrow(() -> new EntidadNoEncontradaException("Tu cuenta no tiene una ficha de cliente", "ClienteEntity"));
        darDeBaja(cliente, motivo.trim(), true);
    }

    private void darDeBaja(ClienteEntity cliente, String motivo, boolean propia) {
        if (!cliente.isEstado()) {
            throw new DatosInvalidoException("La cuenta ya está dada de baja", "ClienteEntity");
        }
        int activas = reservaRepository
                .findByCliente_PublicIdAndEstadoReservaIn(cliente.getPublicId(), ESTADOS_ACTIVOS).size();
        if (activas > 0) {
            throw new AccionInvalidaException(propia
                    ? "No podés darte de baja mientras tengas reservas pendientes o confirmadas (tenés " + activas
                    + "). Cancelalas primero desde Mis reservas"
                    : "El cliente tiene " + activas + (activas == 1 ? " reserva activa" : " reservas activas")
                    + " (pendientes o confirmadas). Cancelalas antes de dar de baja la cuenta", "ClienteEntity");
        }
        cliente.setEstado(false);
        clienteRepository.save(cliente);
        usuarioService.desactivarCuenta(cliente.getUsuario());
        auditoria.registrar(cliente, EAccionCliente.BAJA, motivo,
                propia ? "Baja pedida por el cliente" : "Baja realizada por el personal");
    }

    private ClienteEntity buscar(UUID id) {
        return clienteRepository.findByPublicId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cliente no encontrado: " + id, "ClienteEntity"));
    }

    private void validarDatosUnicos(int dni, String email, String telefono, UUID excluir) {
        clienteRepository.findByDni(dni)
                .filter(otro -> !otro.getPublicId().equals(excluir))
                .ifPresent(otro -> { throw new EntidadExistenteException("Ya existe un cliente con ese DNI", "ClienteEntity"); });
        clienteRepository.findByEmail(email)
                .filter(otro -> !otro.getPublicId().equals(excluir))
                .ifPresent(otro -> { throw new EntidadExistenteException("Ya existe un cliente con ese email", "ClienteEntity"); });
        clienteRepository.findByTelefono(telefono)
                .filter(otro -> !otro.getPublicId().equals(excluir))
                .ifPresent(otro -> { throw new EntidadExistenteException("Ya existe un cliente con ese teléfono", "ClienteEntity"); });
    }

    private ClienteFichaDTO aFicha(ClienteEntity cliente) {
        List<ReservaEntity> todas = reservaRepository.findByCliente_PublicId(cliente.getPublicId());

        List<ReservaResumenDTO> activas = todas.stream()
                .filter(r -> ESTADOS_ACTIVOS.contains(r.getEstadoReserva()))
                .sorted(Comparator.comparing(ReservaEntity::getFechaInicio))
                .map(this::resumen)
                .toList();
        List<ReservaEntity> anteriores = todas.stream()
                .filter(r -> !ESTADOS_ACTIVOS.contains(r.getEstadoReserva()))
                .sorted(Comparator.comparing(ReservaEntity::getFechaInicio).reversed())
                .toList();

        String nombreUsuario = cliente.getUsuario() != null && cliente.getUsuario().getCredencial() != null
                ? cliente.getUsuario().getCredencial().getNombreUsuario() : null;
        return new ClienteFichaDTO(
                clienteMapper.convertToResponseDTO(cliente), nombreUsuario, activas, anteriores.size(),
                anteriores.stream().limit(ULTIMAS_ANTERIORES).map(this::resumen).toList());
    }

    private ReservaResumenDTO resumen(ReservaEntity r) {
        return new ReservaResumenDTO(r.getPublicId(), r.getFechaInicio(), r.getFechaFin(), r.getModalidad(),
                r.getEstadoReserva(), r.getMontoTotal(),
                r.getRecursos().stream().map(RecursoEntity::getNombre).toList());
    }
}