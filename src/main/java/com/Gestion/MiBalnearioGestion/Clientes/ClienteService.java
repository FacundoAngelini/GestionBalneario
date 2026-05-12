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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClientesRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteMapper clienteMapper;
    private final UsuarioMapper usuarioMapper;


    //FALTA AGREGAR FUNCIONES EN LA INTERFAZ ESPECIFICA DE CLIENTE SERVICE POR SI MIGRAMOS
    @Transactional
    public ClienteEntity crearCliente(ClienteDTO dto) {


        if (clienteRepository.findByDni(dto.getDni()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese DNI");
        }
        if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese email"); // no deberia ser runtime
        }

        UsuarioEntity usuario = usuarioMapper.converToEntity(dto.getUsuario(), UsuarioEntity.class);
        UsuarioEntity usuarioGuardado = usuarioRepository.save(usuario);

        ClienteEntity cliente = clienteMapper.convertToEntity(dto, ClienteEntity.class);
        cliente.setUsuario(usuarioGuardado);
        return clienteRepository.save(cliente); //devolver dto al front
    }

    public void borrarCliente(UUID IDpublico)
    {
        ClienteEntity buscado = clienteRepository.
                findByIdPublico(IDpublico)
                .orElseThrow(()-> new EntidadNoEncontradaException("Cliente no se encontró : ", IDpublico.toString()));
        clienteRepository.delete(buscado);
    }

    @Transactional
    public ClienteDTO actualizarCliente(UUID IDpublico, ClienteDTO clienteUpdateDTO) {
        ClienteEntity cliente = clienteRepository
                .findByIdPublico(IDpublico)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cliente no se encontró : ", IDpublico.toString()));


        // Lo compare con el del profe él hace esto porque EMAIL es un Objeto supongo -Fran

        // cliente.setEmail(clienteUpdateDTO.getEmail());

        ClienteEntity actualizado = clienteRepository.save(cliente);

        return clienteMapper.convertToDTO(actualizado);
    }

    public ClienteDTO buscarPorIDpublico(UUID IDpublico) {
        return clienteRepository.
                findByIdPublico(IDpublico)
                .map(clienteMapper::convertToDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("Cliente no se encontró :" , IDpublico.toString()));
    }

    public List<ClienteDTO> findAll() {
        return clienteRepository.findAll().
                stream().
                map(clienteMapper::convertToDTO).
                toList();
    }

}