package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ActualizarClienteDTO;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteDTO;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponseDTO;
import com.Gestion.MiBalnearioGestion.Clientes.dto.CompletarPerfilClienteDTO;

import java.util.List;
import java.util.UUID;

public interface IClienteService {
        ClienteResponseDTO completarPerfil(UUID clientePublicId, CompletarPerfilClienteDTO dto);
        ClienteResponseDTO actualizarCliente(UUID clientePublicId, ActualizarClienteDTO dto);
        void darDeBajaCliente(UUID clientePublicId);
        ClienteResponseDTO buscarPorPublicId(UUID clientePublicId);
        List<ClienteResponseDTO> listarTodos();
    }

