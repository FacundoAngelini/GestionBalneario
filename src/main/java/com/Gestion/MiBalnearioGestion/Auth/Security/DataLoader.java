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
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EtipoRol;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.RolEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.SectorEntity;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.RolRepositorio;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.SectorRepositorio;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.PrecioRecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Repositorios.RecursoRepositorio;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ConfgTemporadaRepository;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ConfiguracionTemporadaEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
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

            System.out.println("Estructura de Seguridad (Auth) cargada con exito");
        }

        if (empleadoRolRepository.count() == 0) {
            empleadoRolRepository.save(RolEntity.builder().tipoRol(EtipoRol.GERENTE).build());
            empleadoRolRepository.save(RolEntity.builder().tipoRol(EtipoRol.MOZO).build());
            System.out.println("Roles de negocio para empleados inicializados");
        }

        SectorEntity sectorSalon;
        if (sectorRepository.count() == 0) {
            sectorRepository.save(SectorEntity.builder().nombre("Administracion").build());
            sectorSalon = sectorRepository.save(SectorEntity.builder().nombre("Salon").build());
            System.out.println("Sectores de negocio inicializados.");
        } else {
            sectorSalon = sectorRepository.findAll().stream()
                    .filter(s -> "Salon".equalsIgnoreCase(s.getNombre()))
                    .findFirst()
                    .orElseGet(() -> sectorRepository.save(SectorEntity.builder().nombre("Salon").build()));
        }

        // 4. ADMINISTRADOR SUPREMO
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

        // 5. CONFIGURACIÓN GLOBAL DE TEMPORADA (ADMIN)
        if (configTemporadaRepository.count() == 0) {
            configTemporadaRepository.save(ConfiguracionTemporadaEntity.builder()
                    .inicioTemporada(LocalDate.of(2026, 12, 1))
                    .fin_temporada(LocalDate.of(2027, 4, 15))
                    .build());
            System.out.println("Configuración global de temporada cargada.");
        }

        // 6. CLIENTE DE PRUEBA COMPLETO (Evita fecha_alta y usuario_id NullException)
        UUID clienteIdPrueba = UUID.fromString("11111111-1111-1111-1111-111111111111");
        if (!clienteRepository.findByPublicId(clienteIdPrueba).isPresent()) {
            UsuarioEntity usuarioCliente = new UsuarioEntity();
            usuarioCliente.setNombreUsuario("juan_cliente_prueba");
            usuarioCliente.setContrasenia(passwordEncoder.encode("cliente123"));
            usuarioCliente = usuarioRepository.save(usuarioCliente);

            ClienteEntity clientePrueba = ClienteEntity.builder()
                    .publicId(clienteIdPrueba)
                    .nombre("Juan")
                    .apellido("Pérez")
                    .dni(12345678)
                    .email("juan.perez@example.com")
                    .telefono("2235555555")
                    .fecha_alta(LocalDate.now())
                    .estado(true)
                    .usuario(usuarioCliente)
                    .build();

            ClienteEntity clienteGuardado= clienteRepository.save(clientePrueba);
            System.out.println("UUID REAL DEL CLIENTE: " + clienteGuardado.getPublicId());
            System.out.println("Cliente de prueba indexado correctamente.");
        }

        // 7. RECURSO (CARPA) CON TARIFA VIGENTE (Evita sector_id NullException)
        UUID carpaIdPrueba = UUID.fromString("22222222-2222-2222-2222-222222222222");
        if (!recursoRepositorio.findByPublicId(carpaIdPrueba).isPresent()) {

            // 1. Creamos el recurso base con su lista de precios inicializada limpia
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

            RecursoEntity guardado = recursoRepositorio.save(carpaPrueba);
            System.out.println("UUID real del recurso: " + guardado.getPublicId());
        }

        System.out.println("Base de datos levantada con exito");
    }
    }
