package com.Gestion.MiBalnearioGestion.Usuarios.Service;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Entity.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.CredencialRepositorio;
import com.Gestion.MiBalnearioGestion.Common.Email.EmailService;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Usuarios.Entity.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.IResetearContraseniaTokenRepository;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Entity.ResetearContraseniaTokenEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.Exception.CuentaEncontradaException;
import com.Gestion.MiBalnearioGestion.Usuarios.Exception.CuentaNoEncontradaException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService implements IUsuarioService {

    private final CredencialRepositorio credencialRepositorio;
    private final IResetearContraseniaTokenRepository resetTokenRepositorio;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void desactivarCuenta(UsuarioEntity usuario) {
        CredencialEntity credencial = credencialRepositorio.findByUsuario(usuario)
                .orElseThrow(() -> new CuentaNoEncontradaException("No tiene credencial asociada", "CredencialEntity"));
        credencial.setEnabled(false);
        credencialRepositorio.save(credencial);
    }

    @Transactional
    @Override
    public void reactivarCuenta(UsuarioEntity usuario) {
        CredencialEntity credencial = credencialRepositorio.findByUsuario(usuario)
                .orElseThrow(() -> new CuentaEncontradaException("No tiene credencial asociada", "CredencialEntity"));
        if (credencial.isEnabled()) {
            throw new CuentaEncontradaException("La cuenta ya esta activa", "CredencialEntity");
        }
        credencial.setEnabled(true);
        credencialRepositorio.save(credencial);
    }

    @Transactional
    @Override
    public void cambiarNombreUsuario(UsuarioEntity usuario, String nuevoNombre) {
        if (credencialRepositorio.findByNombreUsuario(nuevoNombre).isPresent()) {
            throw new CuentaEncontradaException("Ya existe ese nombre de usuario", "CredencialEntity");
        }

        CredencialEntity credencial = credencialRepositorio
                .findByUsuario(usuario)
                .orElseThrow(() -> new CuentaNoEncontradaException("No tiene credencial asociada", "CredencialEntity"));
        credencial.setNombreUsuario(nuevoNombre);
        credencialRepositorio.save(credencial);
    }

    @Transactional
    @Override
    public void cambiarContrasenia(UsuarioEntity usuario,
                                   String contraseniaActual,
                                   String nuevaContrasenia,
                                   PasswordEncoder passwordEncoder) {
        CredencialEntity credencial = credencialRepositorio
                .findByUsuario(usuario).orElseThrow(() -> new CuentaNoEncontradaException("No tiene credencial asociada", "CredencialEntity"));

        if (!passwordEncoder.matches(contraseniaActual, credencial.getContrasenia())) {
            throw new DatosInvalidoException("La contraseña actual es incorrecta", "CredentialEntity");
        }
        credencial.setContrasenia(passwordEncoder.encode(nuevaContrasenia));
        credencialRepositorio.save(credencial);
    }

    @Transactional
    @Override
    public void solicitarResetContrasenia(String nombreUsuario) {
        CredencialEntity credencial = credencialRepositorio
                .findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new CuentaNoEncontradaException("No existe una cuenta con ese usuario", "CredencialEntity"));
        String email = resolverEmail(credencial);
        resetTokenRepositorio.deleteByCredencial(credencial);
        String token = UUID.randomUUID().toString();
        resetTokenRepositorio.save(ResetearContraseniaTokenEntity.builder()
                .token(token)
                .credencial(credencial)
                .expiracion(LocalDateTime.now().plusMinutes(30))
                .usado(false)
                .build());

        emailService.enviarResetContrasenia(email, nombreUsuario, token)
                .thenRun(() -> log.info("Email de reset enviado exitosamente a: {}", email))
                .exceptionally(throwable -> {
                    log.error("Fallo el envío del email a: {}", email, throwable);
                    return null;
                });
    }

    @Transactional
    @Override
    public void resetearContrasenia(String token, String nuevaContrasenia) {
        ResetearContraseniaTokenEntity resetToken = resetTokenRepositorio
                .findByToken(token)
                .orElseThrow(() -> new DatosInvalidoException("El enlace de recuperación no es valido", "CredencialEntity"));

        if (resetToken.isUsado()) {
            throw new DatosInvalidoException("Este enlace ya fue utilizado. Solicita uno nuevo","credencialEntity");
        }
        if (LocalDateTime.now().isAfter(resetToken.getExpiracion())) {
            resetTokenRepositorio.delete(resetToken);
            throw new DatosInvalidoException("El enlace expiró. Solicite uno nuevo", "CredentialEntity");
        }
        CredencialEntity credencial = resetToken.getCredencial();
        credencial.setContrasenia(passwordEncoder.encode(nuevaContrasenia));
        credencialRepositorio.save(credencial);
        resetToken.setUsado(true);
        resetTokenRepositorio.save(resetToken);
    }

    private String resolverEmail(CredencialEntity credencial) {
        UsuarioEntity usuario = credencial.getUsuario();
        if (usuario == null) {
            throw new CuentaEncontradaException("La credencial no tiene usuario asociado", "UsuarioEntity");
        }
        if (usuario.getCliente() != null) {
            return usuario.getCliente().getEmail();
        }
        if (usuario.getEmpleado() != null) {
            return usuario.getEmpleado().getEmail();
        }
        throw new CuentaNoEncontradaException("No se encontró email para el usuario", "UsuarioEntity");
    }
}


