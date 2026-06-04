package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteDTO;
import com.Gestion.MiBalnearioGestion.Clientes.mappers.ClienteMapper;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioMapper;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public ClienteDTO crearCliente(ClienteDTO dto) {


        if (clienteRepository.findByDni(dto.getDni()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese DNI");
        }
        if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese email"); // no deberia ser runtime
        }

        UsuarioEntity usuario = usuarioMapper.convertToEntity(dto.getUsuario(), UsuarioEntity.class);
        UsuarioEntity usuarioGuardado = usuarioRepository.save(usuario);

        ClienteEntity cliente = clienteMapper.convertToEntity(dto, ClienteEntity.class);
        cliente.setUsuario(usuarioGuardado);
        ClienteEntity guardado = clienteRepository.save(cliente);
        return clienteMapper.convertToDTO(guardado);
    }

    public void borrarCliente(UUID IDpublico)
    {
        ClienteEntity buscado = clienteRepository.
                findByPublicId(IDpublico)
                .orElseThrow(()-> new EntidadNoEncontradaException("Cliente no se encontró : ", IDpublico.toString()));
        clienteRepository.delete(buscado);
    }

    @Transactional
    public ClienteDTO actualizarCliente(UUID IDpublico, ClienteDTO clienteUpdateDTO) {
        ClienteEntity cliente = clienteRepository
                .findByPublicId(IDpublico)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cliente no se encontró : ", IDpublico.toString()));

        clienteMapper.updateEntityFromDTO(clienteUpdateDTO, cliente);

        return clienteMapper.convertToDTO(clienteRepository.save(cliente));
    }

    public ClienteDTO buscarPorIDpublico(UUID IDpublico) {
        return clienteRepository.
                findByPublicId(IDpublico)
                .map(clienteMapper::convertToDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cliente no se encontró :" , IDpublico.toString()));
    }

    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll().
                stream().
                map(clienteMapper::convertToDTO).
                toList();
    }



}