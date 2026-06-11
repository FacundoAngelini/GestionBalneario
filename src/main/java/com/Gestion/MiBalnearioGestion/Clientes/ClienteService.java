package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ActualizarClienteDTO;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteDTO;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponseDTO;
import com.Gestion.MiBalnearioGestion.Clientes.dto.CompletarPerfilClienteDTO;
import com.Gestion.MiBalnearioGestion.Clientes.mappers.ClienteMapper;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.Mapper.UsuarioMapper;
import com.Gestion.MiBalnearioGestion.Usuarios.Repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class ClienteService implements IClienteService {

    private final ClientesRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    @Transactional
    public ClienteResponseDTO completarPerfil(UUID clientePublicId, CompletarPerfilClienteDTO dto) {
        ClienteEntity cliente = clienteRepository.findByPublicId(clientePublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Cliente no encontrado", clientePublicId.toString()));

        validarUnicidad(cliente, dto.getEmail(), dto.getDni(), dto.getTelefono());

        clienteMapper.aplicarCompletarPerfil(dto, cliente);
        return clienteMapper.toResponseDTO(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public ClienteResponseDTO actualizarCliente(UUID clientePublicId, ActualizarClienteDTO dto) {
        ClienteEntity cliente = clienteRepository.findByPublicId(clientePublicId)
                .filter(ClienteEntity::isEstado)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Cliente no encontrado o inactivo", clientePublicId.toString()));

        validarUnicidad(cliente, dto.getEmail(), dto.getDni(), dto.getTelefono());

        clienteMapper.aplicarActualizacion(dto, cliente);
        return clienteMapper.toResponseDTO(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public void darDeBajaCliente(UUID clientePublicId) {
        ClienteEntity cliente = clienteRepository.findByPublicId(clientePublicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Cliente no encontrado", clientePublicId.toString()));

        cliente.setEstado(false);

        if (cliente.getUsuario() != null) {
            cliente.getUsuario().setActivo(false);
            if (cliente.getUsuario().getCredencial() != null) {
                cliente.getUsuario().getCredencial().setEnabled(false);
            }
        }

        clienteRepository.save(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorPublicId(UUID clientePublicId) {
        return clienteRepository.findByPublicId(clientePublicId)
                .map(clienteMapper::toResponseDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Cliente no encontrado", clientePublicId.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toResponseDTO)
                .toList();
    }

    private void validarUnicidad(ClienteEntity existente,
                                 String email, Integer dni, String telefono) {
        if (email != null && !email.equals(existente.getEmail()) &&
                clienteRepository.findByEmail(email).isPresent()) {
            throw new EntidadExistenteException("Ya existe un cliente con ese email", "ClienteEntity");
        }
        if (dni != null && !dni.equals(existente.getDni()) &&
                clienteRepository.findByDni(dni).isPresent()) {
            throw new EntidadExistenteException("Ya existe un cliente con ese DNI", "ClienteEntity");
        }
        if (telefono != null && !telefono.equals(existente.getTelefono()) &&
                clienteRepository.findByTelefono(telefono).isPresent()) {
            throw new EntidadExistenteException("Ya existe un cliente con ese teléfono", "ClienteEntity");
        }
    }
}