package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteRequest;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponse;

import java.util.List;
import java.util.UUID;

public interface IClienteService {

    ClienteResponse crearCliente(ClienteRequest dto);

    void borrarCliente(UUID IDpublico);

    ClienteResponse actualizarCliente(UUID IDpublico, ClienteRequest clienteUpdateDTO);

    ClienteResponse buscarPorIDpublico(UUID IDpublico);

    List<ClienteResponse> listarTodos(
            String nombreIgual,    String nombreContiene,
            String apellidoIgual,  String apellidoContiene,
            Integer dniIgual,
            String emailContiene,
            String telefonoIgual,
            Boolean estadoIgual);
}
