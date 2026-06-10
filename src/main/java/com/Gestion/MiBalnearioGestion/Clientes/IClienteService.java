package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteRequest;

import java.util.List;
import java.util.UUID;

public interface IClienteService {

    ClienteRequest crearCliente(ClienteRequest dto);

    public void borrarCliente(UUID IDpublico);

    public ClienteRequest actualizarCliente(UUID IDpublico, ClienteRequest clienteUpdateDTO);

    public ClienteRequest buscarPorIDpublico(UUID IDpublico);

    public List<ClienteRequest> listarTodos();
}
