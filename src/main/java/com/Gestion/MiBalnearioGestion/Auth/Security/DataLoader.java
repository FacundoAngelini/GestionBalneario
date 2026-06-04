package com.Gestion.MiBalnearioGestion.Auth.Security;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.CredencialEntity;
import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.CredencialRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.JWT.JwtService;
import com.Gestion.MiBalnearioGestion.Auth.Permisos.Permisos;
import com.Gestion.MiBalnearioGestion.Auth.Permisos.PermisosEntity;
import com.Gestion.MiBalnearioGestion.Auth.Permisos.Repositorio.PermisosRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.Roles.Repositorio.RolesRepositorio;
import com.Gestion.MiBalnearioGestion.Auth.Roles.Roles;
import com.Gestion.MiBalnearioGestion.Auth.Roles.RolesEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EtipoRol;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.RolEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.SectorEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.RolRepositorio;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PermisosRepositorio permisosRepository;
    private final RolesRepositorio rolesRepository;
    private final CredencialRepositorio credentialsRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RolRepositorio empleadoRolRepository;
    private final SectorRepositorio sectorRepository;

    @Override
    public void run(String... args) throws Exception {

        if (rolesRepository.count() == 0) {
            PermisosEntity verReservas = permisosRepository.save(new PermisosEntity(Permisos.RESERVAS_VER));
            PermisosEntity eliminarReservas = permisosRepository.save(new PermisosEntity(Permisos.RESERVAS_ELIMINAR));

            RolesEntity rolMozo = new RolesEntity(Roles.ROLE_MOZO);
            rolMozo.addPermit(verReservas);
            rolesRepository.save(rolMozo);

            RolesEntity rolAdmin = new RolesEntity(Roles.ROLE_ADMIN);
            rolAdmin.addPermit(verReservas);
            rolAdmin.addPermit(eliminarReservas);
            rolesRepository.save(rolAdmin);

            RolesEntity rolGerente = new RolesEntity(Roles.ROLE_GERENTE);
            rolGerente.addPermit(verReservas);
            rolGerente.addPermit(eliminarReservas);
            rolesRepository.save(rolGerente);

            rolesRepository.save(new RolesEntity(Roles.ROLE_CLIENTE));
            rolesRepository.save(new RolesEntity(Roles.ROLE_CAJERO));
            rolesRepository.save(new RolesEntity(Roles.ROLE_EMPLEADO));
            rolesRepository.save(new RolesEntity(Roles.ROLE_REPARTIDOR));
            rolesRepository.save(new RolesEntity(Roles.ROLE_ADMINISTRACION));

            System.out.println("Estructura de Seguridad (Auth) cargada con éxito");
        }

        if (empleadoRolRepository.count() == 0) {
            empleadoRolRepository.save(RolEntity.builder().tipoRol(EtipoRol.GERENTE).build()); // ID 1
            empleadoRolRepository.save(RolEntity.builder().tipoRol(EtipoRol.MOZO).build());    // ID 2
            System.out.println("Roles de negocio para empleados inicializados");
        }

        if (sectorRepository.count() == 0) {
            sectorRepository.save(SectorEntity.builder().nombre("Administracion").build()); // ID 1
            sectorRepository.save(SectorEntity.builder().nombre("Salon").build());          // ID 2
            System.out.println("Sectores de negocio inicializados.");
        }

        if (!credentialsRepository.existsByNombreUsuario("admin_supremo")) {
            UsuarioEntity usuarioAdmin = new UsuarioEntity();
            usuarioAdmin.setNombreUsuario("admin_supremo");
            usuarioAdmin.setContrasenia(passwordEncoder.encode("admin123"));
            usuarioAdmin = usuarioRepository.save(usuarioAdmin);

            RolesEntity rolAdminDb = rolesRepository.findByRole(Roles.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: El rol ADMIN no existe"));

            Set<RolesEntity> roles = new HashSet<>();
            roles.add(rolAdminDb);

            CredencialEntity credencialPrevia = CredencialEntity.builder()
                    .nombreUsuario("admin_supremo")
                    .roles(roles)
                    .build();

            String tokenInicial = jwtService.generateRefreshToken(credencialPrevia);

            CredencialEntity credencialAdmin = CredencialEntity.builder()
                    .nombreUsuario("admin_supremo")
                    .contrasenia(usuarioAdmin.getContrasenia())
                    .enabled(true)
                    .usuario(usuarioAdmin)
                    .roles(roles)
                    .refreshToken(tokenInicial)
                    .build();

            credentialsRepository.save(credencialAdmin);
            System.out.println("Administrador supremo creado con éxito: admin_supremo");
        }

        System.out.println("Base de datos levantada con éxito");
    }
}