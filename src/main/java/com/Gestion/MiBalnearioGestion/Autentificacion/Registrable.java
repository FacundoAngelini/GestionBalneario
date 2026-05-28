package com.Gestion.MiBalnearioGestion.Autentificacion;

import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioDTO;

import java.util.UUID;
//INTERFAZ PARA UNIFICAR EMPLEADO DTO Y CLIENTE DTO AUTH
public interface Registrable {

    public UsuarioDTO getUser();

    public String getEmail();

    public UUID getPublicId();
}
