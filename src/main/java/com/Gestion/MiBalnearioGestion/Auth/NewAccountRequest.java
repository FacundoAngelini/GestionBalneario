package com.Gestion.MiBalnearioGestion.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewAccountRequest {
    private String nombreUsuario;
    private String contrasenia;
}
