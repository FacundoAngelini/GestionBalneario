package com.Gestion.MiBalnearioGestion.Auth.Roles;

import com.Gestion.MiBalnearioGestion.Auth.Permisos.Permisos;
import com.Gestion.MiBalnearioGestion.Auth.Permisos.PermisosEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="roles")
public class RolesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Roles role;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "roles_permisos",
            joinColumns = @JoinColumn(name = "roles_id"),
            inverseJoinColumns = @JoinColumn(name = "permisos_id"))
    private final Set<PermisosEntity> permits = new HashSet<>();
    public RolesEntity(Roles name) {
        this.role = name;
    }
    public void addPermit(PermisosEntity permit) {
        this.permits.add(permit);
    }

}
