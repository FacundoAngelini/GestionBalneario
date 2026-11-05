package com.Gestion.MiBalnearioGestion.Empleados.Servicio;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Entity.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.CredencialRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.JWT.JwtService;
import com.Gestion.MiBalnearioGestion.Auth.Roles.Repositorio.RolesRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.Roles.Roles;
import com.Gestion.MiBalnearioGestion.Auth.Roles.RolesEntity;
import com.Gestion.MiBalnearioGestion.Auth.Security.ActorActual;
import com.Gestion.MiBalnearioGestion.Common.Email.EmailService;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.AccionInvalidaException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.*;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.*;
import com.Gestion.MiBalnearioGestion.Empleados.Mapper.EmpleadoMapper;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.EmpleadosRepositorio;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.RolRepositorio;
import com.Gestion.MiBalnearioGestion.Sector.Entity.SectorEntity;
import com.Gestion.MiBalnearioGestion.Sector.Repositorio.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Usuarios.Entity.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.Exception.CuentaEncontradaException;
import com.Gestion.MiBalnearioGestion.Usuarios.Repository.UsuarioRepository;
import com.Gestion.MiBalnearioGestion.Usuarios.Service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpleadoService implements IEmpleadoService {

    // Rol de seguridad que se pide -> función del empleado en el balneario
    private static final Map<String, EtipoRol> ROL_DE_NEGOCIO = Map.of(
            "ROLE_ADMIN", EtipoRol.ADMINISTRADOR,
            "ROLE_GERENTE", EtipoRol.GERENTE,
            "ROLE_ADMINISTRACION", EtipoRol.ADMINISTRATIVO,
            "ROLE_CARPERO", EtipoRol.CARPERO,
            "ROLE_CAJERO", EtipoRol.CAJERO,
            "ROLE_MOZO", EtipoRol.MOZO,
            "ROLE_REPARTIDOR", EtipoRol.REPARTIDOR);

    private static final int MIN_MOTIVO = 5;

    private final EmpleadosRepositorio empleadosRepositorio;
    private final UsuarioRepository usuarioRepository;
    private final CredencialRepositorio credentialsRepository;
    private final RolesRepositorio rolesRepository;
    private final RolRepositorio rolRepository;
    private final SectorRepositorio sectorRepository;
    private final EmpleadoMapper empleadoMapper;
    private final IUsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final EmpleadoAuditoria auditoria;
    private final JerarquiaEmpleados jerarquia;
    private final ActorActual actorActual;

    // ---------------------------------------------------------------- alta

    @Transactional
    @Override
    public EmpleadoFichaDTO crearEmpleado(EmpleadoDTO dto) {
        String rolSolicitado = dto.getRolSolicitado().trim().toUpperCase();
        EtipoRol tipoRol = ROL_DE_NEGOCIO.get(rolSolicitado);
        if (tipoRol == null) {
            throw new DatosInvalidoException("El rol solicitado no es válido", "EmpleadoEntity");
        }
        int nivelActor = jerarquia.nivelActor();
        if (!jerarquia.puedeCrear(nivelActor, jerarquia.nivelDe(rolSolicitado))) {
            throw new AccionInvalidaException("No tenés permiso para crear un empleado con ese rol", "EmpleadoEntity");
        }

        validarDatosUnicos(dto.getDni(), dto.getEmail().trim(), dto.getCuit().trim(), null);
        String nombreUsuario = dto.getCredencial().nombreUsuario().trim();
        if (credentialsRepository.existsByNombreUsuario(nombreUsuario)) {
            throw new CuentaEncontradaException("Ya existe ese nombre de usuario", "CredencialEntity");
        }

        SectorEntity sector = sectorRepository.findByPublicId(dto.getSectorPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("El sector elegido no existe", "SectorEntity"));
        RolesEntity rolSeguridad = rolesRepository.findByRole(Roles.valueOf(rolSolicitado))
                .orElseThrow(() -> new EntidadNoEncontradaException("El rol no existe en la base de datos", "RolesEntity"));
        RolEntity rolNegocio = rolRepository.findByTipoRol(tipoRol)
                .orElseThrow(() -> new EntidadNoEncontradaException("La función " + tipoRol + " no existe en la base de datos", "RolEntity"));

        UsuarioEntity usuario = usuarioRepository.save(new UsuarioEntity());
        CredencialEntity credencial = CredencialEntity.builder()
                .nombreUsuario(nombreUsuario)
                .contrasenia(passwordEncoder.encode(dto.getCredencial().contrasenia()))
                .enabled(true)
                .usuario(usuario)
                .roles(Set.of(rolSeguridad))
                .build();
        credencial.setRefreshToken(jwtService.generateRefreshToken(credencial));
        credentialsRepository.save(credencial);

        EmpleadoEntity empleado = empleadoMapper.convertToEntity(dto);
        empleado.setUsuario(usuario);
        empleado.setSector(sector);
        empleado.setRol(rolNegocio);
        empleado = empleadosRepositorio.save(empleado);

        auditoria.registrar(empleado, EAccionEmpleado.ALTA, null, "Alta como " + tipoRol + " en " + sector.getNombre());

        emailService.BienvenidaEmpleado(dto)
                .exceptionally(error -> {
                    log.error("No se pudo enviar el correo de bienvenida a {}", dto.getEmail(), error);
                    return null;
                });

        return aFicha(empleado, nivelActor);
    }

    // ------------------------------------------------------------- consulta

    @Transactional(readOnly = true)
    @Override
    public List<EmpleadoFichaDTO> listarEmpleados(EEstadoEmpleado estado) {
        int nivelActor = jerarquia.nivelActor();
        return empleadosRepositorio.findAll().stream()
                .filter(e -> estado == null || e.getEstadoEmpleado() == estado)
                .filter(e -> jerarquia.puedeVer(nivelActor, nivelDe(e)))
                .map(e -> aFicha(e, nivelActor))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public EmpleadoFichaDTO buscarPorIDpublico(UUID id) {
        EmpleadoEntity empleado = buscar(id);
        int nivelActor = jerarquia.nivelActor();
        boolean esPropio = esElPropio(empleado);
        if (!esPropio && !jerarquia.puedeVer(nivelActor, nivelDe(empleado))) {
            throw new EntidadNoEncontradaException("Empleado no encontrado: " + id, "EmpleadoEntity");
        }
        EmpleadoFichaDTO ficha = aFicha(empleado, nivelActor);
        if (esPropio) ficha.empleado().setSueldo(null); // nadie ve su propio sueldo
        return ficha;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventoEmpleadoDTO> historial(UUID id) {
        EmpleadoEntity empleado = buscar(id);
        if (!esElPropio(empleado) && !jerarquia.puedeVer(jerarquia.nivelActor(), nivelDe(empleado))) {
            throw new EntidadNoEncontradaException("Empleado no encontrado: " + id, "EmpleadoEntity");
        }
        return auditoria.historial(id);
    }

    // ---------------------------------------------------------- modificación

    @Transactional
    @Override
    public EmpleadoFichaDTO actualizarEmpleado(UUID id, EmpleadoUpdateDTO dto) {
        EmpleadoEntity empleado = buscarModificable(id);
        validarDatosUnicos(dto.getDni(), dto.getEmail().trim(), dto.getCuit().trim(), id);

        SectorEntity nuevoSector = null;
        if (dto.getSectorPublicId() != null) {
            nuevoSector = sectorRepository.findByPublicId(dto.getSectorPublicId())
                    .orElseThrow(() -> new EntidadNoEncontradaException("El sector elegido no existe", "SectorEntity"));
        }

        List<String> cambios = detectarCambios(empleado, dto, nuevoSector);
        empleadoMapper.updateEntityFromDTO(dto, empleado);
        if (nuevoSector != null) empleado.setSector(nuevoSector);
        empleado = empleadosRepositorio.save(empleado);

        if (!cambios.isEmpty()) {
            // Solo se anotan los nombres de los campos: no los valores (así no se filtra el sueldo)
            auditoria.registrar(empleado, EAccionEmpleado.DATOS_MODIFICADOS, null, "Se modificó: " + String.join(", ", cambios));
        }
        return aFicha(empleado, jerarquia.nivelActor());
    }

    @Transactional
    @Override
    public EmpleadoFichaDTO cambiarEstado(UUID id, CambioEstadoEmpleadoDTO dto) {
        EmpleadoEntity empleado = buscarModificable(id);
        EEstadoEmpleado actual = empleado.getEstadoEmpleado();
        EEstadoEmpleado nuevo = dto.estado();

        if (actual == nuevo) {
            throw new DatosInvalidoException("El empleado ya está en ese estado", "EmpleadoEntity");
        }
        if (actual == EEstadoEmpleado.INACTIVO && nuevo != EEstadoEmpleado.ACTIVO) {
            throw new DatosInvalidoException("Un empleado dado de baja solo puede reactivarse (pasa a Activo)", "EmpleadoEntity");
        }

        String motivo = dto.motivo() == null ? "" : dto.motivo().trim();
        boolean pideMotivo = nuevo == EEstadoEmpleado.SUSPENDIDO || nuevo == EEstadoEmpleado.LICENCIA
                || nuevo == EEstadoEmpleado.INACTIVO || actual == EEstadoEmpleado.INACTIVO;
        if (pideMotivo && motivo.length() < MIN_MOTIVO) {
            throw new DatosInvalidoException("El motivo es obligatorio (entre 5 y 300 caracteres)", "EmpleadoEntity");
        }

        empleado.setEstadoEmpleado(nuevo);
        // Solo la baja bloquea la cuenta. Suspendido, vacaciones y licencia entran, con acceso restringido (ver JwtService)
        if (nuevo == EEstadoEmpleado.INACTIVO) {
            usuarioService.desactivarCuenta(empleado.getUsuario());
        } else if (actual == EEstadoEmpleado.INACTIVO) {
            usuarioService.reactivarCuenta(empleado.getUsuario());
        }
        empleado = empleadosRepositorio.save(empleado);

        EAccionEmpleado accion = nuevo == EEstadoEmpleado.INACTIVO ? EAccionEmpleado.BAJA
                : actual == EEstadoEmpleado.INACTIVO ? EAccionEmpleado.REACTIVADO
                : EAccionEmpleado.ESTADO_CAMBIADO;
        auditoria.registrar(empleado, accion, motivo.isEmpty() ? null : motivo, actual + " → " + nuevo);
        return aFicha(empleado, jerarquia.nivelActor());
    }

    @Transactional
    @Override
    public EmpleadoResponseDTO actualizarMiFicha(String nombreUsuario, EmpleadoPerfilUpdateDTO dto) {
        EmpleadoEntity empleado = credentialsRepository.findByNombreUsuario(nombreUsuario)
                .map(CredencialEntity::getUsuario)
                .map(UsuarioEntity::getEmpleado)
                .orElseThrow(() -> new EntidadNoEncontradaException("Tu cuenta no tiene una ficha de empleado", "EmpleadoEntity"));

        String email = dto.email().trim();
        empleadosRepositorio.findByEmail(email)
                .filter(otro -> !otro.getPublicId().equals(empleado.getPublicId()))
                .ifPresent(otro -> { throw new EntidadExistenteException("Ya existe un empleado con ese email", "EmpleadoEntity"); });

        List<String> cambios = new ArrayList<>();
        if (!Objects.equals(empleado.getEmail(), email)) cambios.add("email");
        if (!Objects.equals(empleado.getTelefono(), dto.telefono().trim())) cambios.add("teléfono");

        empleado.setEmail(email);
        empleado.setTelefono(dto.telefono().trim());
        empleadosRepositorio.save(empleado);

        if (!cambios.isEmpty()) {
            auditoria.registrar(empleado, EAccionEmpleado.DATOS_MODIFICADOS, null, "Datos propios modificados: " + String.join(", ", cambios));
        }
        EmpleadoResponseDTO respuesta = empleadoMapper.convertToResponseDTO(empleado);
        respuesta.setSueldo(null);
        return respuesta;
    }

    // ---------------------------------------------------------------- interno

    private EmpleadoEntity buscar(UUID id) {
        return empleadosRepositorio.findByPublicId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Empleado no encontrado: " + id, "EmpleadoEntity"));
    }

    /** Verifica que quien actúa pueda ver y modificar a este empleado. */
    private EmpleadoEntity buscarModificable(UUID id) {
        EmpleadoEntity empleado = buscar(id);
        int nivelActor = jerarquia.nivelActor();
        int nivelObjetivo = nivelDe(empleado);
        if (!jerarquia.puedeVer(nivelActor, nivelObjetivo)) {
            throw new EntidadNoEncontradaException("Empleado no encontrado: " + id, "EmpleadoEntity");
        }
        if (!jerarquia.puedeModificar(nivelActor, nivelObjetivo)) {
            throw new AccionInvalidaException("No tenés permiso para modificar a este empleado", "EmpleadoEntity");
        }
        return empleado;
    }

    private void validarDatosUnicos(Integer dni, String email, String cuit, UUID excluir) {
        empleadosRepositorio.findByDni(dni)
                .filter(otro -> !otro.getPublicId().equals(excluir))
                .ifPresent(otro -> { throw new EntidadExistenteException("Ya existe un empleado con ese DNI", "EmpleadoEntity"); });
        empleadosRepositorio.findByEmail(email)
                .filter(otro -> !otro.getPublicId().equals(excluir))
                .ifPresent(otro -> { throw new EntidadExistenteException("Ya existe un empleado con ese email", "EmpleadoEntity"); });
        empleadosRepositorio.findByCuit(cuit)
                .filter(otro -> !otro.getPublicId().equals(excluir))
                .ifPresent(otro -> { throw new EntidadExistenteException("Ya existe un empleado con ese CUIT", "EmpleadoEntity"); });
    }

    private List<String> detectarCambios(EmpleadoEntity e, EmpleadoUpdateDTO d, SectorEntity nuevoSector) {
        List<String> cambios = new ArrayList<>();
        if (!Objects.equals(e.getNombre(), d.getNombre().trim())) cambios.add("nombre");
        if (!Objects.equals(e.getApellido(), d.getApellido().trim())) cambios.add("apellido");
        if (e.getDni() != d.getDni()) cambios.add("DNI");
        if (!Objects.equals(e.getEmail(), d.getEmail().trim())) cambios.add("email");
        if (!Objects.equals(e.getCuit(), d.getCuit().trim())) cambios.add("CUIT");
        if (!Objects.equals(e.getTelefono(), d.getTelefono().trim())) cambios.add("teléfono");
        if (Double.compare(e.getSueldo(), d.getSueldo()) != 0) cambios.add("sueldo");
        DireccionEntity dir = e.getDireccion();
        DireccionDTO nueva = d.getDireccion();
        if (dir == null
                || !Objects.equals(dir.getCalle(), nueva.getCalle().trim())
                || dir.getNumero() != nueva.getNumero()
                || !Objects.equals(dir.getCiudad(), nueva.getCiudad().trim())
                || !Objects.equals(dir.getProvincia(), nueva.getProvincia().trim())) {
            cambios.add("domicilio");
        }
        if (nuevoSector != null && (e.getSector() == null || !e.getSector().getPublicId().equals(nuevoSector.getPublicId()))) {
            cambios.add("sector");
        }
        return cambios;
    }

    private String rolSeguridadDe(EmpleadoEntity e) {
        UsuarioEntity usuario = e.getUsuario();
        if (usuario == null || usuario.getCredencial() == null) return null;
        return usuario.getCredencial().getRoles().stream()
                .map(r -> r.getRole().name())
                .findFirst()
                .orElse(null);
    }

    private int nivelDe(EmpleadoEntity e) {
        return jerarquia.nivelDe(rolSeguridadDe(e));
    }

    private boolean esElPropio(EmpleadoEntity e) {
        UsuarioEntity usuario = e.getUsuario();
        return usuario != null && usuario.getCredencial() != null
                && usuario.getCredencial().getNombreUsuario().equals(actorActual.obtener().usuario());
    }

    private EmpleadoFichaDTO aFicha(EmpleadoEntity e, int nivelActor) {
        String rol = rolSeguridadDe(e);
        String nombreUsuario = e.getUsuario() != null && e.getUsuario().getCredencial() != null
                ? e.getUsuario().getCredencial().getNombreUsuario() : null;
        boolean editable = jerarquia.puedeModificar(nivelActor, jerarquia.nivelDe(rol));
        return new EmpleadoFichaDTO(empleadoMapper.convertToResponseDTO(e), nombreUsuario, rol, editable);
    }
}