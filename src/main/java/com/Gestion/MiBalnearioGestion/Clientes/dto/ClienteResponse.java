package com.Gestion.MiBalnearioGestion.Clientes.dto;

import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioDTO;
import jakarta.validation.constraints.*;

import java.util.UUID;

public class ClienteResponse {

    private String nombre;

    private String apellido;

    private UUID publicId;

    private int dni;

    private String email;

    private String telefono;

}
