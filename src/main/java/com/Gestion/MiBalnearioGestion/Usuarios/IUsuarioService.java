package com.Gestion.MiBalnearioGestion.Usuarios;

import com.Gestion.MiBalnearioGestion.Auth.NewAccountRequest;

import java.util.List;
import java.util.UUID;

public interface IUsuarioService {
     void desactivarCuenta(UsuarioEntity usuario);
}
