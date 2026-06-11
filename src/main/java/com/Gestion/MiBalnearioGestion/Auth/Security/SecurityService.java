package com.Gestion.MiBalnearioGestion.Auth.Security;

import com.Gestion.MiBalnearioGestion.Clientes.ClientesRepository;
import com.Gestion.MiBalnearioGestion.Usuarios.Repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("securityService")
public class SecurityService {
    private final UsuarioRepository usuarioRepository;
    private final ClientesRepository clientesRepository;

    public SecurityService(UsuarioRepository usuarioRepository,  ClientesRepository clientesRepository) {
        this.usuarioRepository = usuarioRepository;
        this.clientesRepository = clientesRepository;
    }

    public boolean esElMismoUsuario(Authentication authentication, UUID idPublica) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String usernameAutenticado = authentication.getName();

        // Busca en la base de datos si el UUID de la URL le pertenece al usuario logueado
        return usuarioRepository.findByPublicId(idPublica)
                .map(usuario -> usuario.getCredencial().getNombreUsuario().equals(usernameAutenticado))
                .orElse(false);
    }

    public boolean esElMismoCliente(Authentication authentication, UUID clientePublicId) {
        String username = authentication.getName();
        return clientesRepository.findByPublicId(clientePublicId)
                .map(c -> c.getUsuario().getCredencial().getNombreUsuario().equals(username))
                .orElse(false);
    }
}
