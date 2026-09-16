package com.Gestion.MiBalnearioGestion.Usuarios.Service;

import com.Gestion.MiBalnearioGestion.Usuarios.Entity.UsuarioEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface IUsuarioService {
     void desactivarCuenta(UsuarioEntity usuario);
    void reactivarCuenta(UsuarioEntity usuario);
    void cambiarNombreUsuario(UsuarioEntity usuario, String nuevoNombre);
    void cambiarContrasenia(UsuarioEntity usuario,
                            String contraseniaActual,
                            String nuevaContrasenia,
                            PasswordEncoder passwordEncoder);
    void solicitarResetContrasenia(String nombreUsuario);
    void resetearContrasenia(String token, String nuevaContrasenia);
}
