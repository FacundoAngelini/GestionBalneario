package com.Gestion.MiBalnearioGestion.Clientes.Service;

import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteRequest;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponse;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO.CambioContraseniaRequest;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO.CambioNombreUsuarioRequest;

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

    void cambiarNombreUsuarioCliente(UUID publicId, CambioNombreUsuarioRequest request);
    void cambiarContraseniaCliente(UUID publicId, CambioContraseniaRequest request);
    ClienteResponse reactivarCliente(UUID publicId);
}
