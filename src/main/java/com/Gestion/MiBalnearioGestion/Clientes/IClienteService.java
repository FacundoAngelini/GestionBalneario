package com.Gestion.MiBalnearioGestion.Clientes;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteDTO;

public interface IClienteService {
    ClienteEntity crearCliente(ClienteDTO dto);
}
