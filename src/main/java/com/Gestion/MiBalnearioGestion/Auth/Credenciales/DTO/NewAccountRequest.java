package com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewAccountRequest {
    private String nombreUsuario;
    private String contrasenia;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private int dni;
}
