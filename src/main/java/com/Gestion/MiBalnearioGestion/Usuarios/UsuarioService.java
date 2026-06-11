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

    // No expuesto como endpoint — lo llaman ClienteService y EmpleadoService
    @Transactional
    public void desactivarCuenta(UsuarioEntity usuario) {
        CredencialEntity credencial = credencialRepositorio
                .findByUsuario(usuario)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "No tiene credencial asociada", "CredencialEntity"));

        credencial.setEnabled(false);
        credencialRepositorio.save(credencial);
        // La baja lógica del empleado/cliente la maneja cada service propio
    }

}
