package com.Gestion.MiBalnearioGestion.Usuarios.Servicio;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.CredencialRepositorio;
import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.ClientesRepository;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.EmpleadosRepositorio;
import com.Gestion.MiBalnearioGestion.Usuarios.*;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.ActualizarPerfilRequest;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.CambiarContraseniaRequest;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.CambiarNombreUsuarioDTO;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.UsuarioDTO;
import com.Gestion.MiBalnearioGestion.Usuarios.Exception.ExContraseniaIncorrecta;
import com.Gestion.MiBalnearioGestion.Usuarios.Mapper.UsuarioMapper;
import com.Gestion.MiBalnearioGestion.Usuarios.Repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepositorio;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final ClientesRepository clientesRepository;
    private final EmpleadosRepositorio empleadosRepositorio;
    private final CredencialRepositorio credencialRepositorio;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> buscarTodosUsuarios(String username,
                                                String usernameContiene,
                                                Boolean activos,
                                                Boolean noActivos) {
        PredicateSpecification<UsuarioEntity> spec = PredicateSpecification.allOf(
                UsuarioSpecification.usernameIgual(username),
                UsuarioSpecification.usernameContiene(usernameContiene),
                UsuarioSpecification.activos(activos),
                UsuarioSpecification.noActivos(noActivos)
        );

        return usuarioRepositorio.findAll(spec)
                .stream()
                .map(usuarioMapper::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorIdPublica(UUID idPublica) {
        return usuarioRepositorio.findByPublicId(idPublica)
                .map(usuarioMapper::convertToDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontro un usuario con esa id", "UsuarioEntity"));
    }

    @Override
    @Transactional
    public UsuarioDTO actualizarUsuario(UUID idPublica, ActualizarPerfilRequest request) {
        UsuarioEntity usuario = usuarioRepositorio.findByPublicId(idPublica)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "No se encontro un usuario con ese id", "UsuarioEntity"));

        if (usuario.getCliente() != null) {
            actualizarCliente(usuario.getCliente(), request);
        } else if (usuario.getEmpleado() != null) {
            actualizarEmpleado(usuario.getEmpleado(), request);
        } else {
            throw new EntidadNoEncontradaException(
                    "No existe ninguna entidad asociada al perfil", "UsuarioEntity");
        }

        return usuarioMapper.convertToDTO(usuarioRepositorio.save(usuario));
    }

    @Override
    @Transactional
    public void cambiarContrasenia(UUID id, CambiarContraseniaRequest request) {
        UsuarioEntity usuario = usuarioRepositorio.findByPublicId(id)
                .filter(UsuarioEntity::isActivo)
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontro un usuario con ese id", "UsuarioEntity"));

        CredencialEntity credencial = usuario.getCredencial();
        if (credencial == null) {
            throw new EntidadNoEncontradaException("El usuario no tiene credencial asociada al perfil", "UsuarioEntity");
        }

        if (!passwordEncoder.matches(request.getContraseniaActual(), credencial.getContrasenia())) {
            throw new ExContraseniaIncorrecta("La contraseña no es correcta", "Contrasenia");
        }

        credencial.setContrasenia(passwordEncoder.encode(request.getContraseniaNueva()));
        credencialRepositorio.save(credencial);
    }

    @Override
    @Transactional
    public void darDeBaja(UUID idPublica) {
        UsuarioEntity usuario = usuarioRepositorio.findByPublicId(idPublica)
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontro un usuario con ese id", "UsuarioEntity"));

        usuario.setActivo(false);

        if (usuario.getCliente() != null) {
            usuario.getCliente().setEstado(false);
        }

        if (usuario.getCredencial() != null) {
            usuario.getCredencial().setEnabled(false);
        }

        usuarioRepositorio.save(usuario);
    }

    private void actualizarCliente(ClienteEntity cliente, ActualizarPerfilRequest request) {
        if (!cliente.getEmail().equals(request.getEmail()) &&
                clientesRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EntidadExistenteException("Ya existe un cliente con ese email", "ClienteEntity");
        }
        if (cliente.getDni() != request.getDni() &&
                clientesRepository.findByDni(request.getDni()).isPresent()) {
            throw new EntidadExistenteException("Ya existe un cliente con ese DNI", "ClienteEntity");
        }
        if (!cliente.getTelefono().equals(request.getTelefono()) &&
                clientesRepository.findByTelefono(request.getTelefono()).isPresent()) {
            throw new EntidadExistenteException("Ya existe un cliente con ese teléfono", "ClienteEntity");
        }

        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());
        cliente.setDni(request.getDni());
        clientesRepository.save(cliente);
    }

    @Transactional
    public void cambiarNombreUsuario(UUID usuarioPublicId, CambiarNombreUsuarioDTO request) {
        if (credencialRepositorio.findByNombreUsuario(request.getNuevoNombreUsuario()).isPresent()) {
            throw new EntidadExistenteException(
                    "Ese nombre de usuario ya está en uso", "CredencialEntity");
        }

        UsuarioEntity usuario = usuarioRepositorio.findByPublicId(usuarioPublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Usuario no encontrado", "UsuarioEntity"));

        usuario.getCredencial().setNombreUsuario(request.getNuevoNombreUsuario());
        credencialRepositorio.save(usuario.getCredencial());
    }

    private void actualizarEmpleado(EmpleadoEntity empleado, ActualizarPerfilRequest request) {
        if (!empleado.getEmail().equals(request.getEmail()) &&
                empleadosRepositorio.findByEmail(request.getEmail()).isPresent()) {
            throw new EntidadExistenteException("Ya existe un empleado con ese email", "EmpleadoEntity");
        }
        if (empleado.getDni() != request.getDni() &&
                empleadosRepositorio.findByDni(request.getDni()).isPresent()) {
            throw new EntidadExistenteException("Ya existe un empleado con ese DNI", "EmpleadoEntity");
        }

        empleado.setNombre(request.getNombre());
        empleado.setApellido(request.getApellido());
        empleado.setEmail(request.getEmail());
        empleado.setTelefono(request.getTelefono());
        empleado.setDni(request.getDni());
        empleadosRepositorio.save(empleado);
    }
}
