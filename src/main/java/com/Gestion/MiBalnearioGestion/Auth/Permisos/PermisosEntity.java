package com.Gestion.MiBalnearioGestion.Auth.Permisos;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="permisos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermisosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    Permisos permiso;
}
