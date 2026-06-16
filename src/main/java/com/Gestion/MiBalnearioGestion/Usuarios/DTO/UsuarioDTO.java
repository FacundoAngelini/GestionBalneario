package com.Gestion.MiBalnearioGestion.Usuarios.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
    public class UsuarioDTO {
    private UUID clienteId;
    private String nombreUsuario;
    private Set<String> roles;
}
