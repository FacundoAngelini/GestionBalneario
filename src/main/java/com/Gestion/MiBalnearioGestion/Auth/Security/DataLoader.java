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
import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.ClientesRepository;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.*;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.EmpleadosRepositorio;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.RolRepositorio;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EtipoRol;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.RolEntity;
import com.Gestion.MiBalnearioGestion.Sector.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.RecursoRepositorio;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ConfgTemporadaRepository;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ConfiguracionTemporadaEntity;
import com.Gestion.MiBalnearioGestion.Sector.SectorEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

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
    private final ClientesRepository clienteRepository;
    private final RecursoRepositorio recursoRepositorio;
    private final ConfgTemporadaRepository configTemporadaRepository;
    private final EmpleadosRepositorio empleadosRepositorio;

    @Override
    public void run(String... args) throws Exception {

        // 1. PERMISOS Y ROLES DE SPRING SECURITY
        if (rolesRepository.count() == 0) {
            PermisosEntity verReservas = permisosRepository.save(
                    new PermisosEntity(Permisos.RESERVAS_VER));
            PermisosEntity eliminarReservas = permisosRepository.save(
                    new PermisosEntity(Permisos.RESERVAS_ELIMINAR));

            RolesEntity rolAdmin = new RolesEntity(Roles.ROLE_ADMIN);
            rolAdmin.addPermit(verReservas);
            rolAdmin.addPermit(eliminarReservas);
            rolesRepository.save(rolAdmin);

            RolesEntity rolGerente = new RolesEntity(Roles.ROLE_GERENTE);
            rolGerente.addPermit(verReservas);
            rolGerente.addPermit(eliminarReservas);
            rolesRepository.save(rolGerente);

            RolesEntity rolMozo = new RolesEntity(Roles.ROLE_MOZO);
            rolMozo.addPermit(verReservas);
            rolesRepository.save(rolMozo);

            rolesRepository.save(new RolesEntity(Roles.ROLE_CLIENTE));
            rolesRepository.save(new RolesEntity(Roles.ROLE_CAJERO));
            rolesRepository.save(new RolesEntity(Roles.ROLE_EMPLEADO));
            rolesRepository.save(new RolesEntity(Roles.ROLE_REPARTIDOR));
            rolesRepository.save(new RolesEntity(Roles.ROLE_ADMINISTRACION));

            System.out.println("Roles y permisos de seguridad cargados.");
        }

        // 2. ROLES DE NEGOCIO (EtipoRol)
        if (empleadoRolRepository.count() == 0) {
            empleadoRolRepository.save(RolEntity.builder().tipoRol(EtipoRol.GERENTE).build());
            empleadoRolRepository.save(RolEntity.builder().tipoRol(EtipoRol.MOZO).build());
            empleadoRolRepository.save(RolEntity.builder().tipoRol(EtipoRol.CAJERO).build());
            empleadoRolRepository.save(RolEntity.builder().tipoRol(EtipoRol.CARPERO).build());
            empleadoRolRepository.save(RolEntity.builder().tipoRol(EtipoRol.COCINERO).build());
            empleadoRolRepository.save(RolEntity.builder().tipoRol(EtipoRol.GUARDAVIDAS).build());
            empleadoRolRepository.save(RolEntity.builder().tipoRol(EtipoRol.REPARTIDOR).build());
            empleadoRolRepository.save(RolEntity.builder().tipoRol(EtipoRol.ADMINISTRATIVO).build());
            System.out.println("Roles de negocio cargados.");
        }

        // 3. SECTORES
        SectorEntity sectorAdmin;
        SectorEntity sectorSalon;
        if (sectorRepository.count() == 0) {
            sectorAdmin = sectorRepository.save(
                    SectorEntity.builder().nombre("Administracion").build());
            sectorSalon = sectorRepository.save(
                    SectorEntity.builder().nombre("Salon").build());
            sectorRepository.save(SectorEntity.builder().nombre("Cocina").build());
            sectorRepository.save(SectorEntity.builder().nombre("Playa").build());
            System.out.println("Sectores cargados.");
        } else {
            sectorAdmin = sectorRepository.findByNombreIgnoreCase("Administracion")
                    .orElseGet(() -> sectorRepository.save(
                            SectorEntity.builder().nombre("Administracion").build()));
            sectorSalon = sectorRepository.findByNombreIgnoreCase("Salon")
                    .orElseGet(() -> sectorRepository.save(
                            SectorEntity.builder().nombre("Salon").build()));
        }

        // 4. ADMIN SUPREMO
        if (!credentialsRepository.existsByNombreUsuario("admin_supremo")) {
            UsuarioEntity usuarioAdmin = new UsuarioEntity();
            usuarioAdmin = usuarioRepository.save(usuarioAdmin);

            RolesEntity rolAdminDb = rolesRepository.findByRole(Roles.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("El rol ADMIN no existe"));

            CredencialEntity credencialPrevia = CredencialEntity.builder()
                    .nombreUsuario("admin_supremo")
                    .roles(Set.of(rolAdminDb))
                    .build();

            CredencialEntity credencialAdmin = CredencialEntity.builder()
                    .nombreUsuario("admin_supremo")
                    .contrasenia(passwordEncoder.encode("admin123"))
                    .enabled(true)
                    .usuario(usuarioAdmin)
                    .roles(Set.of(rolAdminDb))
                    .refreshToken(jwtService.generateRefreshToken(credencialPrevia))
                    .build();

            credentialsRepository.save(credencialAdmin);
            System.out.println("Admin supremo creado: admin_supremo / admin123");
        }

        // 5. GERENTE DE PRUEBA
        if (!credentialsRepository.existsByNombreUsuario("gerente_prueba")) {
            UsuarioEntity usuarioGerente = new UsuarioEntity();
            usuarioGerente = usuarioRepository.save(usuarioGerente);

            RolesEntity rolGerenteDb = rolesRepository.findByRole(Roles.ROLE_GERENTE)
                    .orElseThrow(() -> new RuntimeException("El rol GERENTE no existe"));

            RolEntity rolNegocioGerente = empleadoRolRepository.findByTipoRol(EtipoRol.GERENTE)
                    .orElseThrow(() -> new RuntimeException("El rol de negocio GERENTE no existe"));

            CredencialEntity credencialPrevia = CredencialEntity.builder()
                    .nombreUsuario("gerente_prueba")
                    .roles(Set.of(rolGerenteDb))
                    .build();

            CredencialEntity credencialGerente = CredencialEntity.builder()
                    .nombreUsuario("gerente_prueba")
                    .contrasenia(passwordEncoder.encode("gerente123"))
                    .enabled(true)
                    .usuario(usuarioGerente)
                    .roles(Set.of(rolGerenteDb))
                    .refreshToken(jwtService.generateRefreshToken(credencialPrevia))
                    .build();

            credentialsRepository.save(credencialGerente);

            EmpleadoEntity gerentePrueba = EmpleadoEntity.builder()
                    .nombre("Juan")
                    .apellido("Gerente")
                    .dni(99999999)
                    .email("gerente@balneario.com")
                    .sueldo(300000)
                    .cuit("20999999990")
                    .estadoEmpleado(EEstadoEmpleado.ACTIVO)
                    .telefono("2235550001")
                    .direccion(new DireccionEntity("San Martin", 100, "Mar del Plata", "Buenos Aires"))
                    .sector(sectorAdmin)
                    .rol(rolNegocioGerente)
                    .usuario(usuarioGerente)
                    .build();

            empleadosRepositorio.save(gerentePrueba);
            System.out.println("Gerente de prueba creado: gerente_prueba / gerente123");
        }

        // 6. CLIENTE DE PRUEBA
        UUID clienteIdPrueba = UUID.fromString("11111111-1111-1111-1111-111111111111");
        if (!clienteRepository.findByPublicId(clienteIdPrueba).isPresent()) {
            UsuarioEntity usuarioCliente = new UsuarioEntity();
            usuarioCliente = usuarioRepository.save(usuarioCliente);

            RolesEntity rolClienteDb = rolesRepository.findByRole(Roles.ROLE_CLIENTE)
                    .orElseThrow(() -> new RuntimeException("El rol CLIENTE no existe"));

            CredencialEntity credencialPrevia = CredencialEntity.builder()
                    .nombreUsuario("juan_cliente")
                    .roles(Set.of(rolClienteDb))
                    .build();

            CredencialEntity credencialCliente = CredencialEntity.builder()
                    .nombreUsuario("juan_cliente")
                    .contrasenia(passwordEncoder.encode("juan123"))
                    .enabled(true)
                    .usuario(usuarioCliente)
                    .roles(Set.of(rolClienteDb))
                    .refreshToken(jwtService.generateRefreshToken(credencialPrevia))
                    .build();

            credentialsRepository.save(credencialCliente);

            ClienteEntity clientePrueba = ClienteEntity.builder()
                    .publicId(clienteIdPrueba)
                    .nombre("Juan")
                    .apellido("Perez")
                    .dni(12345678)
                    .email("juan.perez@example.com")
                    .telefono("2235555555")
                    .fechaAlta(LocalDate.now())
                    .estado(true)
                    .usuario(usuarioCliente)
                    .build();

            clienteRepository.save(clientePrueba);
            System.out.println("Cliente de prueba creado: juan_cliente / juan123");
        }

        // 7. CONFIGURACIÓN DE TEMPORADA
        if (configTemporadaRepository.count() == 0) {
            configTemporadaRepository.save(ConfiguracionTemporadaEntity.builder()
                    .inicioTemporada(LocalDate.of(2026, 12, 1))
                    .fin_temporada(LocalDate.of(2027, 4, 15))
                    .build());
            System.out.println("Configuracion de temporada cargada.");
        }

        // 8. RECURSO DE PRUEBA
        UUID carpaIdPrueba = UUID.fromString("22222222-2222-2222-2222-222222222222");
        if (!recursoRepositorio.findByPublicId(carpaIdPrueba).isPresent()) {
            RecursoEntity carpaPrueba = RecursoEntity.builder()
                    .publicId(carpaIdPrueba)
                    .nombre("Carpa Premium N° 10")
                    .esReservable(true)
                    .sector(sectorSalon)
                    .precioRecurso(new ArrayList<>())
                    .build();

            PrecioRecursoEntity precioVigente = PrecioRecursoEntity.builder()
                    .precio(2.0)
                    .fechaVigencia(LocalDate.of(2026, 1, 1))
                    .fechaCaducada(LocalDate.of(2026, 12, 31))
                    .recurso(carpaPrueba)
                    .build();

            carpaPrueba.getPrecioRecurso().add(precioVigente);
            recursoRepositorio.save(carpaPrueba);
            System.out.println("Recurso de prueba cargado.");
        }

        System.out.println("Base de datos levantada con exito");
    }
}