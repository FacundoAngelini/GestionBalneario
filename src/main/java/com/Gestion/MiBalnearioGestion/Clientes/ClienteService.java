package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteRequest;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponse;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioMapper;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService implements IClienteService {

    private final ClientesRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteMapper clienteMapper;
    private final UsuarioMapper usuarioMapper;


    @Transactional
    public ClienteResponse crearCliente(ClienteRequest dto) {


        if (clienteRepository.findByDni(dto.getDni()).isPresent()) {
            throw new EntidadExistenteException("Ya existe un cliente con ese DNI","ClienteEntity");
        }
        if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EntidadExistenteException("Ya existe un cliente registrado con ese email", "ClienteEntity");
        }


        UsuarioEntity usuario = usuarioMapper.convertToEntity(dto.getUsuario(), UsuarioEntity.class);
        UsuarioEntity usuarioGuardado = usuarioRepository.save(usuario);

        ClienteEntity cliente = clienteMapper.convertToEntity(dto, ClienteEntity.class);
        cliente.setPublicId(UUID.randomUUID());
        cliente.setFecha_alta(LocalDate.now());
        cliente.setUsuario(usuarioGuardado);
        ClienteEntity guardado = clienteRepository.save(cliente);
        return clienteMapper.convertToResponseDTO(guardado);
    }

    @Transactional
    public void borrarCliente(UUID IDpublico) {
        ClienteEntity buscado = clienteRepository.findByPublicId(IDpublico)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cliente no se encontró : ", IDpublico.toString()));

        UsuarioEntity usuarioAsociado = buscado.getUsuario();

        clienteRepository.delete(buscado);

        if (usuarioAsociado != null) {
            usuarioRepository.delete(usuarioAsociado);
        }
    }

    @Transactional
    public ClienteResponse actualizarCliente(UUID IDpublico, ClienteRequest clienteUpdateDTO) {
        ClienteEntity cliente = clienteRepository
                .findByPublicId(IDpublico)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cliente no se encontro : ", IDpublico.toString()));

        clienteMapper.updateEntityFromDTO(clienteUpdateDTO, cliente);

        return clienteMapper.convertToResponseDTO(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public ClienteDTO buscarPorIDpublico(UUID IDpublico) {
        return clienteRepository.
                findByPublicId(IDpublico)
                .map(clienteMapper::convertToDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cliente no se encontro :" , IDpublico.toString()));
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll().
                stream().
                map(clienteMapper::convertToResponseDTO).
                toList();
    }



}