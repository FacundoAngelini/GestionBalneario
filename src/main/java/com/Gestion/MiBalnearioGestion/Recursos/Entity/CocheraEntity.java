package com.Gestion.MiBalnearioGestion.Recursos.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Table(name="Cocheras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CocheraEntity extends RecursoEntity {
    @Column(name="numero_cochera", unique=true, nullable = false)
    private int numero_cochera;
}
