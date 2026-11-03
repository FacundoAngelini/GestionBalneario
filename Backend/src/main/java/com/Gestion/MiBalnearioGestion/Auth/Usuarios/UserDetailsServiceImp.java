package com.Gestion.MiBalnearioGestion.Auth.Usuarios;

import com.Gestion.MiBalnearioGestion.Auth.Credenciales.Repositorio.CredencialRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImp implements UserDetailsService {
    private final CredencialRepositorio credentialsRepository;
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(@NonNull String nombreUsuario) throws UsernameNotFoundException {
        return credentialsRepository.
                findByNombreUsuario(nombreUsuario).
                orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

}
