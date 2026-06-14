package com.Gestion.MiBalnearioGestion.Empleados.Servicio;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.CredencialRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.JWT.JwtService;
import com.Gestion.MiBalnearioGestion.Auth.Roles.Repositorio.RolesRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.Roles.Roles;
import com.Gestion.MiBalnearioGestion.Auth.Roles.RolesEntity;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoResponseDTO;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoUpdateDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.*;
import com.Gestion.MiBalnearioGestion.Empleados.Mapper.EmpleadoMapper;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.EmpleadosRepositorio;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.RolRepositorio;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EtipoRol;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.RolEntity;
import com.Gestion.MiBalnearioGestion.Sector.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Sector.SectorEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmpleadoService implements IEmpleadoService {
    private final EmpleadosRepositorio empleadosRepositorio;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoMapper empleadoMapper;
    private final UsuarioService usuarioService;
    private final CredencialRepositorio credentialsRepository;
    private final RolesRepositorio rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RolRepositorio rolRepository;
    private final SectorRepositorio sectorRepository;


    @Transactional
    @Override
    public EmpleadoResponseDTO crearEmpleado(EmpleadoDTO dtoEmpleado) {


        if (empleadosRepositorio.findByDni(dtoEmpleado.getDni()).isPresent())
            throw new EntidadExistenteException("Ya existe un empleado con ese DNI", "EmpleadoEntity");
        if (empleadosRepositorio.findByEmail(dtoEmpleado.getEmail()).isPresent())
            throw new EntidadExistenteException("Ya existe un empleado con ese email", "EmpleadoEntity");
        if (empleadosRepositorio.findByCuit(dtoEmpleado.getCuit()).isPresent())
            throw new EntidadExistenteException("Ya existe un empleado con ese cuit", "EmpleadoEntity");

        if (credentialsRepository.findByNombreUsuario(dtoEmpleado.getCredencial().nombreUsuario()).isPresent())
            throw new EntidadExistenteException("Ya existe ese nombre de usuario", "CredencialEntity");


        boolean esGerente = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.toUpperCase().contains("GERENTE"));

        String rolSolicitado = dtoEmpleado.getRolSolicitado().toUpperCase();
        if (esGerente && (rolSolicitado.contains("GERENTE") || rolSolicitado.contains("ADMIN")))
            throw new AccessDeniedException("Un Gerente solo puede crear empleados de rango menor");

        UsuarioEntity nuevoUsuario = new UsuarioEntity();
        nuevoUsuario = usuarioRepository.save(nuevoUsuario);


        Roles enumRol = Roles.valueOf(rolSolicitado);
        RolesEntity rolDb = rolesRepository.findByRole(enumRol)
                .orElseThrow(() -> new EntidadNoEncontradaException("El rol no existe en la base de datos", "RolEntity"));

        CredencialEntity nuevaCredencial = CredencialEntity.builder()
                .nombreUsuario(dtoEmpleado.getCredencial().nombreUsuario())
                .contrasenia(passwordEncoder.encode(dtoEmpleado.getCredencial().contrasenia()))
                .enabled(true)
                .usuario(nuevoUsuario)
                .roles(Set.of(rolDb))
                .build();
        nuevaCredencial.setRefreshToken(jwtService.generateRefreshToken(nuevaCredencial));
        credentialsRepository.save(nuevaCredencial);

        EmpleadoEntity empleado = empleadoMapper.convertToEntity(dtoEmpleado);
        empleado.setUsuario(nuevoUsuario);
        empleado.setEstadoEmpleado(dtoEmpleado.getEstado());

        if (dtoEmpleado.getSector() != null && dtoEmpleado.getSector().getNombre() != null) {
            SectorEntity sector = sectorRepository.findByNombreIgnoreCase(dtoEmpleado.getSector().getNombre())
                    .orElseThrow(() -> new EntidadNoEncontradaException("El sector '" + dtoEmpleado.getSector().getNombre() + "' no existe", "SectorEntity"));
            empleado.setSector(sector);
        } else {
            SectorEntity sectorDefecto = sectorRepository.findByNombreIgnoreCase("Administración")
                    .orElseThrow(() -> new EntidadNoEncontradaException("Sector por defecto 'Administración' no inicializado.", "SectorEntity"));
            empleado.setSector(sectorDefecto);
        }

        try {
            // 1. Limpiamos el String: "ROLE_MOZO" -> "MOZO", "MOZO" -> "MOZO"
            String nombreRolPuro = rolSolicitado.replace("ROLE_", "").trim().toUpperCase();

            // 2. Traducción de equivalencias (Mapeo manual para excepciones)
            if (nombreRolPuro.equals("EMPLEADO") || nombreRolPuro.equals("ADMINISTRACION")) {
                nombreRolPuro = "ADMINISTRATIVO"; // Lo volcamos al enum que sí tenés
            }

            // 3. Convertimos al Enum de negocio de forma segura
            EtipoRol tipoRolNegocio = EtipoRol.valueOf(nombreRolPuro);

            // 4. Buscamos en el repositorio de negocio
            RolEntity rolNegocio = rolRepository.findByTipoRol(tipoRolNegocio)
                    .orElseThrow(() -> new EntidadNoEncontradaException(
                            "El rol de negocio para " + tipoRolNegocio + " no existe en la base de datos", "RolEntity"));

            empleado.setRol(rolNegocio);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El rol solicitado '" + rolSolicitado +
                    "' no se pudo asociar a ningún rol de negocio válido (Opciones: CARPERO, COCINERO, MOZO, CAJERO, GERENTE, GUARDAVIDAS, REPARTIDOR, ADMINISTRATIVO).");
        }

        return empleadoMapper.convertToResponseDTO(empleadosRepositorio.save(empleado));
    }
    @Transactional
    @Override
    public void borrarEmpleado(UUID IDpublico)
    {
        EmpleadoEntity buscado = empleadosRepositorio
                .findByPublicId(IDpublico)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Empleado no se encontro: ", IDpublico.toString()));
        buscado.setEstadoEmpleado(EEstadoEmpleado.INACTIVO);
        empleadosRepositorio.save(buscado);
        if (buscado.getUsuario() != null) {
            usuarioService.desactivarCuenta(buscado.getUsuario());
        }
    }

    @Transactional
    @Override
    public EmpleadoResponseDTO actualizarEmpleado(UUID IDpublico, EmpleadoUpdateDTO dto)
    {
        EmpleadoEntity empleado = empleadosRepositorio.findByPublicId(IDpublico)
                 .orElseThrow(() -> new EntidadNoEncontradaException("Empleado no encontrado "+IDpublico.toString(), "EmpleadoEntity"));

        if (empleadosRepositorio.findByDni(dto.getDni()).isPresent())
            throw new EntidadExistenteException("Ya existe un empleado con ese DNI", "EmpleadoEntity");
        if (empleadosRepositorio.findByEmail(dto.getEmail()).isPresent())
            throw new EntidadExistenteException("Ya existe un empleado con ese email", "EmpleadoEntity");
        if (empleadosRepositorio.findByCuit(dto.getCuit()).isPresent())
            throw new EntidadExistenteException("Ya existe un empleado con ese cuit", "EmpleadoEntity");

        empleadoMapper.updateEntityFromDTO(dto, empleado);
        if(dto.getRolPublicId()!=null){RolEntity rol = rolRepository.findByPublicId(dto.getRolPublicId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Rol no encontrado"+ dto.getRolPublicId().toString(),"RolEntity"));
            empleado.setRol(rol);
        }
        if(dto.getSectorPublicId()!=null){
            SectorEntity sector = sectorRepository.findByPublicId(dto.getSectorPublicId())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Sector no encontrado"+ dto.getSectorPublicId().toString(),"SectorEntity"));
            empleado.setSector(sector);
        }

        return empleadoMapper.convertToResponseDTO(empleadosRepositorio.save(empleado));
    }


    @Override
    public EmpleadoResponseDTO buscarPorIDpublico(UUID IDpublico) {
        return empleadosRepositorio.
                findByPublicId(IDpublico)
                .map(empleadoMapper::convertToResponseDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("Empleado no se encontro :" , IDpublico.toString()));
    }

    @Override
    public List<EmpleadoResponseDTO> listarEmpleados(  Integer dniIgual, Integer dniContiene,
                                               String nombreIgual, String nombreContiene,
                                               String apellidoIgual, String apellidoContiene,
                                               String telefonoIgual, String telefonoContiene,
                                               String cuitIgual, String cuitContiene,
                                               Double sueldoIgual, Double sueldoMenor, Double sueldoMayor,
                                               String sectorIgual, String sectorContiene,
                                               String rolIgual, String rolContiene,
                                               String calleIgual, String calleContiene,
                                               Integer numeroIgual, Integer numeroContiene,
                                               String ciudadIgual, String ciudadContiene,
                                               String provinciaIgual, String provinciaContiene,
                                               EEstadoEmpleado estadoIgual) {

        PredicateSpecification<EmpleadoEntity> spec =
                PredicateSpecification.allOf(
                        EmpleadoSpecification.dniIgual(dniIgual),
                        EmpleadoSpecification.dniContiene(dniContiene),
                        EmpleadoSpecification.nombreIgual(nombreIgual),
                        EmpleadoSpecification.nombreContiene(nombreContiene),
                        EmpleadoSpecification.apellidoIgual(apellidoIgual),
                        EmpleadoSpecification.apellidoContiene(apellidoContiene),
                        EmpleadoSpecification.telefonoIgual(telefonoIgual),
                        EmpleadoSpecification.telefonoContiene(telefonoContiene),
                        EmpleadoSpecification.cuitIgual(cuitIgual),
                        EmpleadoSpecification.cuitContiene(cuitContiene),
                        EmpleadoSpecification.sueldoIgual(sueldoIgual),
                        EmpleadoSpecification.sueldoMayor(sueldoMayor),
                        EmpleadoSpecification.sueldoMenor(sueldoMenor),
                        EmpleadoSpecification.sectorIgual(sectorIgual),
                        EmpleadoSpecification.sectorContiene(sectorContiene),
                        EmpleadoSpecification.rolIgual(rolIgual),
                        EmpleadoSpecification.rolContiene(rolContiene),
                        EmpleadoSpecification.calleIgual(calleIgual),
                        EmpleadoSpecification.calleContiene(calleContiene),
                        EmpleadoSpecification.ciudadIgual(ciudadIgual),
                        EmpleadoSpecification.ciudadContiene(ciudadContiene),
                        EmpleadoSpecification.provinciaIgual(provinciaIgual),
                        EmpleadoSpecification.provinciaContiene(provinciaContiene),
                        EmpleadoSpecification.estadoIgual(estadoIgual)
                );
        return empleadosRepositorio
                .findAll(spec)
                .stream()
                .map(empleadoMapper::convertToResponseDTO)
                .toList();
    }

    @Transactional
    @Override
    public EmpleadoResponseDTO reactivarEmpleado(UUID publicId) {
        EmpleadoEntity empleado = empleadosRepositorio
                .findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Empleado no encontrado: ", publicId.toString()));

        if (empleado.getEstadoEmpleado() == EEstadoEmpleado.ACTIVO) {
            throw new IllegalStateException("El empleado ya está activo");
        }

        empleado.setEstadoEmpleado(EEstadoEmpleado.ACTIVO);
        empleadosRepositorio.save(empleado);

        if (empleado.getUsuario() != null) {
            usuarioService.reactivarCuenta(empleado.getUsuario());
        }

        return empleadoMapper.convertToResponseDTO(empleado);
    }

    @Transactional
    @Override
    public void cambiarNombreUsuarioEmpleado(UUID publicId, CambioNombreUsuarioRequest request) {
        EmpleadoEntity empleado = empleadosRepositorio
                .findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Empleado no encontrado: ", publicId.toString()));

        usuarioService.cambiarNombreUsuario(empleado.getUsuario(), request.nuevoNombreUsuario());
    }

    @Transactional
    @Override
    public void cambiarContraseniaEmpleado(UUID publicId, CambioContraseniaRequest request) {
        EmpleadoEntity empleado = empleadosRepositorio
                .findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Empleado no encontrado: ", publicId.toString()));

        usuarioService.cambiarContrasenia(
                empleado.getUsuario(),
                request.contraseniaActual(),
                request.nuevaContrasenia(),
                passwordEncoder);
    }


}
