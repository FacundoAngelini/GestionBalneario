package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteDTO;

import java.util.List;
import java.util.UUID;

public interface IClienteService {

    //FALTA AGREGAR FUNCIONES DE CLIENTE SERVICE
    ClienteDTO crearCliente(ClienteDTO dto);

    public void borrarCliente(UUID IDpublico);

    public ClienteDTO actualizarCliente(UUID IDpublico, ClienteDTO clienteUpdateDTO);

    public ClienteDTO buscarPorIDpublico(UUID IDpublico);

    public List<ClienteDTO> listarTodos();
}
