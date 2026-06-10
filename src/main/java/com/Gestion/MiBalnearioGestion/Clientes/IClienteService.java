package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteRequest;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponse;

import java.util.List;
import java.util.UUID;

public interface IClienteService {

    ClienteResponse crearCliente(ClienteRequest dto);

    public void borrarCliente(UUID IDpublico);

    public ClienteResponse actualizarCliente(UUID IDpublico, ClienteRequest clienteUpdateDTO);

    public ClienteResponse buscarPorIDpublico(UUID IDpublico);

    public List<ClienteResponse> listarTodos();
}
