package com.Gestion.MiBalnearioGestion.Usuarios;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.CredencialRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.NewAccountRequest;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService{

    private final CredencialRepositorio credencialRepositorio;

    @Transactional
    public void desactivarCuenta(UsuarioEntity usuario) {
        CredencialEntity credencial = credencialRepositorio
                .findByUsuario(usuario)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "No tiene credencial asociada", "CredencialEntity"));

        credencial.setEnabled(false);
        credencialRepositorio.save(credencial);
    }

    @Transactional
    public void reactivarCuenta(UsuarioEntity usuario) {
        CredencialEntity credencial = credencialRepositorio
                .findByUsuario(usuario)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "No tiene credencial asociada", "CredencialEntity"));

        if (credencial.isEnabled()) {
            throw new IllegalStateException("La cuenta ya está activa");
        }

        credencial.setEnabled(true);
        credencialRepositorio.save(credencial);
    }

    @Transactional
    public void cambiarNombreUsuario(UsuarioEntity usuario, String nuevoNombre) {
        if (credencialRepositorio.findByNombreUsuario(nuevoNombre).isPresent()) {
            throw new EntidadExistenteException(
                    "Ya existe ese nombre de usuario", "CredencialEntity");
        }

        CredencialEntity credencial = credencialRepositorio
                .findByUsuario(usuario)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "No tiene credencial asociada", "CredencialEntity"));

        credencial.setNombreUsuario(nuevoNombre);
        credencialRepositorio.save(credencial);
    }

    @Transactional
    public void cambiarContrasenia(UsuarioEntity usuario,
                                   String contraseniaActual,
                                   String nuevaContrasenia,
                                   PasswordEncoder passwordEncoder) {
        CredencialEntity credencial = credencialRepositorio
                .findByUsuario(usuario)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "No tiene credencial asociada", "CredencialEntity"));

        // verifica que la contraseña actual sea correcta
        if (!passwordEncoder.matches(contraseniaActual, credencial.getContrasenia())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        credencial.setContrasenia(passwordEncoder.encode(nuevaContrasenia));
        credencialRepositorio.save(credencial);
    }

}
