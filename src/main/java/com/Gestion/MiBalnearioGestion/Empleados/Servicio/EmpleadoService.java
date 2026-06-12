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
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.RolEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.SectorEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Mapper.EmpleadoMapper;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.EmpleadosRepositorio;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.RolRepositorio;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioMapper;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmpleadoService implements IEmpleadoService {
    private final EmpleadosRepositorio empleadosRepositorio;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoMapper empleadoMapper;
    private final UsuarioMapper usuarioMapper;
    private final CredencialRepositorio credentialsRepository;
    private final RolesRepositorio rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RolRepositorio rolRepository;
    private final SectorRepositorio sectorRepository;


    @Transactional
    @Override
    public EmpleadoResponseDTO crearEmpleado(EmpleadoDTO dtoEmpleado) {

        // validaciones de duplicados
        if (empleadosRepositorio.findByDni(dtoEmpleado.getDni()).isPresent())
            throw new EntidadExistenteException("Ya existe un empleado con ese DNI", "EmpleadoEntity");
        if (empleadosRepositorio.findByEmail(dtoEmpleado.getEmail()).isPresent())
            throw new EntidadExistenteException("Ya existe un empleado con ese email", "EmpleadoEntity");
        if (empleadosRepositorio.findByCuit(dtoEmpleado.getCuit()).isPresent())
            throw new EntidadExistenteException("Ya existe un empleado con ese cuit", "EmpleadoEntity");

        // validación de username duplicado
        if (credentialsRepository.findByNombreUsuario(dtoEmpleado.getCredencial().nombreUsuario()).isPresent())
            throw new EntidadExistenteException("Ya existe ese nombre de usuario", "CredencialEntity");

        // validación de rol
        boolean esGerente = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.toUpperCase().contains("GERENTE"));

        String rolSolicitado = dtoEmpleado.getRolSolicitado().toUpperCase();
        if (esGerente && (rolSolicitado.contains("GERENTE") || rolSolicitado.contains("ADMIN")))
            throw new AccessDeniedException("Un Gerente solo puede crear empleados de rango menor");

        // UsuarioEntity: solo publicId, sin datos de auth
        UsuarioEntity nuevoUsuario = new UsuarioEntity();
        nuevoUsuario = usuarioRepository.save(nuevoUsuario);

        // roles de Spring Security
        Roles enumRol = Roles.valueOf(rolSolicitado);
        RolesEntity rolDb = rolesRepository.findByRole(enumRol)
                .orElseThrow(() -> new RuntimeException("El rol no existe en la base de datos."));

        // CredencialEntity: todo lo de auth viene de dtoEmpleado.getCredencial()
        CredencialEntity nuevaCredencial = CredencialEntity.builder()
                .nombreUsuario(dtoEmpleado.getCredencial().nombreUsuario())
                .contrasenia(passwordEncoder.encode(dtoEmpleado.getCredencial().contrasenia()))
                .enabled(true)
                .usuario(nuevoUsuario)
                .roles(Set.of(rolDb))
                .build();
        nuevaCredencial.setRefreshToken(jwtService.generateRefreshToken(nuevaCredencial));
        credentialsRepository.save(nuevaCredencial);

        // EmpleadoEntity
        EmpleadoEntity empleado = empleadoMapper.convertToEntity(dtoEmpleado);
        empleado.setUsuario(nuevoUsuario);
        empleado.setEstadoEmpleado(dtoEmpleado.getEstado());

        if (dtoEmpleado.getSector() != null && dtoEmpleado.getSector().getPublicId() != null) {
            SectorEntity sector = sectorRepository.findByPublicId(dtoEmpleado.getSector().getPublicId())
                    .orElseThrow(() -> new RuntimeException("El sector no existe."));
            empleado.setSector(sector);
        }

        if (dtoEmpleado.getRol() != null && dtoEmpleado.getRol().getPublicId() != null) {
            RolEntity rolNegocio = rolRepository.findByPublicId(dtoEmpleado.getRol().getPublicId())
                    .orElseThrow(() -> new RuntimeException("El rol de negocio no existe."));
            empleado.setRol(rolNegocio);
        }

        return empleadoMapper.convertToResponseDTO(empleadosRepositorio.save(empleado));
    }
    @Transactional
    @Override
    public void borrarEmpleado(UUID IDpublico)
    {
        EmpleadoEntity buscado = empleadosRepositorio.
                findByPublicId(IDpublico)
                .orElseThrow(()-> new EntidadNoEncontradaException("Empleado no se encontró : ", IDpublico.toString()));
        buscado.setEstadoEmpleado(EEstadoEmpleado.INACTIVO);
        empleadosRepositorio.save(buscado);
    }

    @Transactional
    @Override
    public EmpleadoResponseDTO actualizarEmpleado(UUID IDpublico, EmpleadoDTO empleadoDto) {
        EmpleadoEntity empleado = empleadosRepositorio
                .findByPublicId(IDpublico)
                .orElseThrow(() -> new EntidadNoEncontradaException("Empleado no se encontró : ", IDpublico.toString()));

        empleadoMapper.updateEntityFromDTO(empleadoDto, empleado);

        return empleadoMapper.convertToResponseDTO(empleadosRepositorio.save(empleado));
    }


    @Override
    public EmpleadoResponseDTO buscarPorIDpublico(UUID IDpublico) {
        return empleadosRepositorio.
                findByPublicId(IDpublico)
                .map(empleadoMapper::convertToResponseDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("Empleado no se encontró :" , IDpublico.toString()));
    }

    @Override
    public List<EmpleadoResponseDTO> buscarTodos(Integer dniIgual,
                                         Integer dniContiene,
                                         String nombreIgual,
                                         String nombreContiene,
                                         String apellidoIgual,
                                         String apellidoContiene,
                                         String telefonoIgual,
                                         String telefonoContiene,
                                         String cuitIgual,
                                         String cuitContiene,
                                         Double sueldoIgual,
                                         Double sueldoMenor,
                                         Double sueldoMayor,
                                         String sectorIgual,
                                         String sectorContiene,
                                         String rolIgual,
                                         String rolContiene,
                                         String calleIgual,
                                         String calleContiene,
                                         Integer numeroIgual,
                                         Integer numeroContiene,
                                         String ciudadIgual,
                                         String ciudadContiene,
                                         String provinciaIgual,
                                         String provinciaContiene,
                                         EEstadoEmpleado estadoIgual,
                                         EEstadoEmpleado estadoActivo,
                                         EEstadoEmpleado estadoInactivo) {

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
                        EmpleadoSpecification.sueldoMenor(sueldoMenor),
                        EmpleadoSpecification.sueldoMayor(sueldoMayor),
                        EmpleadoSpecification.sectorIgual(sectorIgual),
                        EmpleadoSpecification.sectorContiene(sectorContiene),
                        EmpleadoSpecification.rolIgual(rolIgual),
                        EmpleadoSpecification.rolContiene(rolContiene),
                        EmpleadoSpecification.calleIgual(calleIgual),
                        EmpleadoSpecification.calleContiene(calleContiene),
                        EmpleadoSpecification.numeroIgual(numeroIgual),
                        EmpleadoSpecification.numeroContiene(numeroContiene),
                        EmpleadoSpecification.ciudadIgual(ciudadIgual),
                        EmpleadoSpecification.ciudadContiene(ciudadContiene),
                        EmpleadoSpecification.provinciaIgual(provinciaIgual),
                        EmpleadoSpecification.provinciaContiene(provinciaContiene),
                        EmpleadoSpecification.estadoIgual(estadoIgual),
                        EmpleadoSpecification.estadoActivo(estadoActivo),
                        EmpleadoSpecification.estadoInactivo(estadoInactivo)
                );


        return empleadosRepositorio.findAll(spec)
                .stream()
                .map(empleadoMapper::convertToResponseDTO)
                .toList();
    }

}