package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.CredencialRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.JWT.JwtService;
import com.Gestion.MiBalnearioGestion.Auth.Roles.Repositorio.RolesRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.Roles.Roles;
import com.Gestion.MiBalnearioGestion.Auth.Roles.RolesEntity;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteRequest;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponse;
import com.Gestion.MiBalnearioGestion.Common.Email.EmailService;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Usuarios.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class ClienteService implements IClienteService {

    private final ClientesRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CredencialRepositorio credencialRepositorio;
    private final RolesRepositorio rolesRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ClienteMapper clienteMapper;
    private final UsuarioService usuarioService;
    private final EmailService emailService;

    @Transactional
    @Override
    public ClienteResponse crearCliente(ClienteRequest dto) {
        if (credencialRepositorio.findByNombreUsuario(
                dto.getCredencial().nombreUsuario()).isPresent())
            throw new EntidadExistenteException(
                    "Ya existe ese nombre de usuario", "CredencialEntity");
        if (clienteRepository.findByDni(dto.getDni()).isPresent())
            throw new EntidadExistenteException(
                    "Ya existe un cliente con ese DNI", "ClienteEntity");
        if (clienteRepository.findByEmail(dto.getEmail()).isPresent())
            throw new EntidadExistenteException(
                    "Ya existe un cliente con ese email", "ClienteEntity");

        UsuarioEntity nuevoUsuario = new UsuarioEntity();

        RolesEntity rolCliente = rolesRepositorio.findByRole(Roles.ROLE_CLIENTE)
                .orElseThrow(() -> new RuntimeException(
                        "El rol ROLE_CLIENTE no existe en la base de datos."));

        CredencialEntity nuevaCredencial = CredencialEntity.builder()
                .nombreUsuario(dto.getCredencial().nombreUsuario())
                .contrasenia(passwordEncoder.encode(dto.getCredencial().contrasenia()))
                .enabled(true)
                .usuario(nuevoUsuario)
                .roles(Set.of(rolCliente))
                .build();
        nuevaCredencial.setRefreshToken(jwtService.generateRefreshToken(nuevaCredencial));

        ClienteEntity nuevoCliente = ClienteEntity.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .dni(dto.getDni())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .fechaAlta(LocalDate.now())
                .estado(true)
                .usuario(nuevoUsuario)
                .build();

        nuevoUsuario.setCredencial(nuevaCredencial);
        nuevoUsuario.setCliente(nuevoCliente);

        usuarioRepository.save(nuevoUsuario);
        credencialRepositorio.save(nuevaCredencial);
        ClienteEntity guardado = clienteRepository.save(nuevoCliente);

         emailService.BienvenidaCliente(dto);

        return clienteMapper.convertToResponseDTO(guardado);
    }

    @Transactional
    @Override
    public void borrarCliente(UUID publicId) {
        ClienteEntity cliente = clienteRepository.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Cliente no encontrado: ", publicId.toString()));

        cliente.setEstado(false);
        clienteRepository.save(cliente);

        if (cliente.getUsuario() != null) {
            usuarioService.desactivarCuenta(cliente.getUsuario());
        }
    }

    @Transactional
    @Override
    public ClienteResponse actualizarCliente(UUID publicId, ClienteRequest dto) {
        ClienteEntity cliente = clienteRepository.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Cliente no encontrado: ", publicId.toString()));

        if (!cliente.getEmail().equals(dto.getEmail()) &&
                clienteRepository.findByEmail(dto.getEmail()).isPresent())
            throw new EntidadExistenteException(
                    "Ese email ya esta registrado", "ClienteEntity");

        if (cliente.getDni() != dto.getDni() &&
                clienteRepository.findByDni(dto.getDni()).isPresent())
            throw new EntidadExistenteException(
                    "Ese DNI ya esta registrado", "ClienteEntity");

        clienteMapper.updateEntityFromDTO(dto, cliente);
        return clienteMapper.convertToResponseDTO(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    @Override
    public ClienteResponse buscarPorIDpublico(UUID publicId) {
        return clienteRepository.findByPublicId(publicId)
                .map(clienteMapper::convertToResponseDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Cliente no encontrado: ", publicId.toString()));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ClienteResponse> listarTodos(
            String nombreIgual,    String nombreContiene,
            String apellidoIgual,  String apellidoContiene,
            Integer dniIgual,
            String emailContiene,
            String telefonoIgual,
            Boolean estadoIgual) {

        List<PredicateSpecification<ClienteEntity>> specs = new ArrayList<>();

        if (nombreIgual != null)      specs.add(ClienteSpecification.nombreIgual(nombreIgual));
        if (nombreContiene != null)   specs.add(ClienteSpecification.nombreContiene(nombreContiene));
        if (apellidoIgual != null)    specs.add(ClienteSpecification.apellidoIgual(apellidoIgual));
        if (apellidoContiene != null) specs.add(ClienteSpecification.apellidoContiene(apellidoContiene));
        if (dniIgual != null)         specs.add(ClienteSpecification.dniIgual(dniIgual));
        if (emailContiene != null)    specs.add(ClienteSpecification.emailContiene(emailContiene));
        if (telefonoIgual != null)    specs.add(ClienteSpecification.telefonoIgual(telefonoIgual));
        if (estadoIgual != null)      specs.add(ClienteSpecification.estadoIgual(estadoIgual));

        if (specs.isEmpty()) {
            return clienteRepository.findAll()
                    .stream()
                    .map(clienteMapper::convertToResponseDTO)
                    .toList();
        }

        return clienteRepository.findAll(PredicateSpecification.allOf(specs))
                .stream()
                .map(clienteMapper::convertToResponseDTO)
                .toList();
    }

    @Transactional
    @Override
    public ClienteResponse reactivarCliente(UUID publicId) {
        ClienteEntity cliente = clienteRepository
                .findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Cliente no encontrado: ", publicId.toString()));

        if (cliente.isEstado()) {
            throw new IllegalStateException("El cliente ya está activo");
        }

        cliente.setEstado(true);
        clienteRepository.save(cliente);

        if (cliente.getUsuario() != null) {
            usuarioService.reactivarCuenta(cliente.getUsuario());
        }

        return clienteMapper.convertToResponseDTO(cliente);
    }

    @Transactional
    @Override
    public void cambiarNombreUsuarioCliente(UUID publicId, CambioNombreUsuarioRequest request) {
        ClienteEntity cliente = clienteRepository
                .findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Cliente no encontrado: ", publicId.toString()));

        usuarioService.cambiarNombreUsuario(cliente.getUsuario(), request.nuevoNombreUsuario());
    }

    @Transactional
    @Override
    public void cambiarContraseniaCliente(UUID publicId, CambioContraseniaRequest request) {
        ClienteEntity cliente = clienteRepository
                .findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Cliente no encontrado: ", publicId.toString()));

        usuarioService.cambiarContrasenia(
                cliente.getUsuario(),
                request.contraseniaActual(),
                request.nuevaContrasenia(),
                passwordEncoder);
    }
}