package com.Gestion.MiBalnearioGestion.Auth.Security;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.CredencialRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.Permisos.Permisos;
import com.Gestion.MiBalnearioGestion.Auth.Permisos.PermisosEntity;
import com.Gestion.MiBalnearioGestion.Auth.Permisos.Repositorio.PermisosRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.Roles.Repositorio.RolesRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.Roles.Roles;
import com.Gestion.MiBalnearioGestion.Auth.Roles.RolesEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PermisosRepositorio permisosRepository;
    private final RolesRepositorio rolesRepository;
    private final CredencialRepositorio credentialsRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (rolesRepository.count() > 0) return;

        // 1. Guardamos dos permisos para probar
        PermisosEntity verReservas = permisosRepository.save(new PermisosEntity(Permisos.RESERVAS_VER));
        PermisosEntity eliminarReservas = permisosRepository.save(new PermisosEntity(Permisos.RESERVAS_ELIMINAR));

        // 2. Creamos y asignamos los permisos a los Roles
        RolesEntity rolMozo = new RolesEntity(Roles.ROLE_MOZO);
        rolMozo.addPermit(verReservas);
        rolesRepository.save(rolMozo);

        RolesEntity rolAdmin = new RolesEntity(Roles.ROLE_ADMIN);
        rolAdmin.addPermit(verReservas);
        rolAdmin.addPermit(eliminarReservas);
        rolesRepository.save(rolAdmin);

        // Guardamos los demás roles para que ya existan vacíos en la base de datos
        rolesRepository.save(new RolesEntity(Roles.ROLE_CLIENTE));
        rolesRepository.save(new RolesEntity(Roles.ROLE_CAJERO));
        rolesRepository.save(new RolesEntity(Roles.ROLE_EMPLEADO));
        rolesRepository.save(new RolesEntity(Roles.ROLE_REPARTIDOR));
        rolesRepository.save(new RolesEntity(Roles.ROLE_ADMINISTRACION));

        // 3. Creamos el usuario Mozo de prueba
        UsuarioEntity uMozo = new UsuarioEntity();
        uMozo.setNombreUsuario("juan_mozo");
        uMozo.setContrasenia(passwordEncoder.encode("mozo123"));
        uMozo = usuarioRepository.save(uMozo);

        CredencialEntity cMozo = CredencialEntity.builder()
                .nombreUsuario(uMozo.getNombreUsuario())
                .contrasenia(uMozo.getContrasenia())
                .enabled(true)
                .refreshToken("token_inicial_mozo")
                .usuario(uMozo)
                .roles(Set.of(rolMozo))
                .build();
        credentialsRepository.save(cMozo);

        // 4. Creamos el usuario Admin de prueba
        UsuarioEntity uAdmin = new UsuarioEntity();
        uAdmin.setNombreUsuario("facu_admin");
        uAdmin.setContrasenia(passwordEncoder.encode("admin123"));
        uAdmin = usuarioRepository.save(uAdmin);

        CredencialEntity cAdmin = CredencialEntity.builder()
                .nombreUsuario(uAdmin.getNombreUsuario())
                .contrasenia(uAdmin.getContrasenia())
                .enabled(true)
                .refreshToken("token_inicial_admin")
                .usuario(uAdmin)
                .roles(Set.of(rolAdmin))
                .build();
        credentialsRepository.save(cAdmin);

        System.out.println("Base de datoslevantada con exito y sin errores de usuario");
    }
}