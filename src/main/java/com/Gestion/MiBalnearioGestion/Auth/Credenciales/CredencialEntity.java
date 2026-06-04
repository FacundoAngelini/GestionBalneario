package com.Gestion.MiBalnearioGestion.Auth.Credenciales;

import com.Gestion.MiBalnearioGestion.Auth.Roles.RolesEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="credenciales")
public class CredencialEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false, name="nombre_usuario")
    private String nombreUsuario;

    @Column(nullable = false, name="contrasenia")
    private String contrasenia;

    @Column(name = "refresh_token", length = 512, unique = true, nullable = false)
    private String refreshToken;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean enabled;

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", unique = true)
    private UsuarioEntity usuario;

    @ManyToMany(cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
    @JoinTable(
            name = "credencial_roles",
            joinColumns = @JoinColumn(name = "credencial_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id")
    )
    private Set<RolesEntity> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        if (roles != null) {
            roles.forEach(rol -> {

                if (rol != null && rol.getRole() != null) {
                    authorities.add(new SimpleGrantedAuthority(rol.getRole().name()));
                    if (rol.getPermits() != null) {
                        rol.getPermits().forEach(permisoEntity -> {
                            if (permisoEntity != null && permisoEntity.getNombrePermiso() != null) {
                                authorities.add(new SimpleGrantedAuthority(permisoEntity.getNombrePermiso().name()));
                            }
                        });
                    }
                }
            });
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.contrasenia;
    }

    @Override
    public String getUsername() {
        return this.nombreUsuario;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.enabled);
    }
}
