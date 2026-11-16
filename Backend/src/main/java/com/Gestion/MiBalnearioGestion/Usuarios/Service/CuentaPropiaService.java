package com.Gestion.MiBalnearioGestion.Usuarios.Service;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO.CambioContraseniaRequest;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO.CambioNombreUsuarioRequest;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Entity.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.CredencialRepositorio;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Usuarios.Entity.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cambios que cualquier usuario hace sobre su propia cuenta (cliente, empleado o administrador supremo). */
@Service
@RequiredArgsConstructor
public class CuentaPropiaService {

    private final CredencialRepositorio credencialRepositorio;
    private final IUsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void cambiarContrasenia(String nombreUsuario, CambioContraseniaRequest request) {
        usuarioService.cambiarContrasenia(usuarioDe(nombreUsuario), request.contraseniaActual(),
                request.nuevaContrasenia(), passwordEncoder);
    }

    @Transactional
    public void cambiarNombreUsuario(String nombreUsuario, CambioNombreUsuarioRequest request) {
        usuarioService.cambiarNombreUsuario(usuarioDe(nombreUsuario), request.nuevoNombreUsuario().trim());
    }

    private UsuarioEntity usuarioDe(String nombreUsuario) {
        return credencialRepositorio.findByNombreUsuario(nombreUsuario)
                .map(CredencialEntity::getUsuario)
                .orElseThrow(() -> new EntidadNoEncontradaException("No se encontró la cuenta", "CredencialEntity"));
    }
}