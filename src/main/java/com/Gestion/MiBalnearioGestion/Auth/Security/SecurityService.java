package com.Gestion.MiBalnearioGestion.Auth.Security;

import com.Gestion.MiBalnearioGestion.Clientes.Repository.ClientesRepository;
import com.Gestion.MiBalnearioGestion.Empleados.Repositorio.EmpleadosRepositorio;
import com.Gestion.MiBalnearioGestion.Pedidos.Repository.IPedidoRepository;
import com.Gestion.MiBalnearioGestion.Reservas.Repositorios.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final ClientesRepository clientesRepository;
    private final EmpleadosRepositorio empleadosRepositorio;
    private final ReservaRepository reservaRepository;
    private final IPedidoRepository pedidoRepository;

    public boolean esElPropioCliente(UUID publicId) {
        String usernameLogueado = obtenerUsernameActual();

        return clientesRepository.findByPublicId(publicId)
                .map(cliente -> cliente.getUsuario())
                .map(usuario -> usuario.getCredencial())
                .map(credencial -> credencial.getNombreUsuario())
                .map(username -> username.equals(usernameLogueado))
                .orElse(false);
    }


    public boolean esElPropioEmpleado(UUID publicId) {

         String usernameLogueado = obtenerUsernameActual();

            return empleadosRepositorio.findByPublicId(publicId)
                .map(empleado -> empleado.getUsuario())
                .map(usuario -> usuario.getCredencial())
                .map(credencial -> credencial.getNombreUsuario())
                .map(username -> username.equals(usernameLogueado))
                .orElse(false);
    }


    public boolean esDuenioDeLaReserva(UUID reservaPublicId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esPersonalElevado = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(rol -> rol.equals("ROLE_ADMIN") || rol.equals("ROLE_GERENTE") || rol.equals("ROLE_ADMINISTRACION"));

        if (esPersonalElevado) {
            return true;
        }

        String usernameLogueado = auth.getName();

        return reservaRepository.findByPublicId(reservaPublicId)
                .map(reserva -> reserva.getCliente())
                .map(cliente -> cliente.getUsuario())
                .map(usuario -> usuario.getCredencial())
                .map(credencial -> credencial.getNombreUsuario())
                .map(username -> username.equals(usernameLogueado))
                .orElse(false);
    }

    public boolean esDuenioDelPedido(UUID pedidoPublicId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esPersonalElevado = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(rol -> rol.equals("ROLE_ADMIN") || rol.equals("ROLE_GERENTE") || rol.equals("ROLE_ADMINISTRACION") || rol.equals("ROLE_CAJERO"));

        if (esPersonalElevado) {
            return true;
        }

        String usernameLogueado = auth.getName();

        return pedidoRepository.findByPublicId(pedidoPublicId)
                .map(pedido -> pedido.getCliente())
                .map(cliente -> cliente.getUsuario())
                .map(usuario -> usuario.getCredencial())
                .map(credencial -> credencial.getNombreUsuario())
                .map(username -> username.equals(usernameLogueado))
                .orElse(false);
    }

    private String obtenerUsernameActual() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}