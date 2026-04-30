package com.Gestion.MiBalnearioGestion.Recursos;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "carpas")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CarpaEntity extends RecursoEntity{
   @Column(name="numero_carpa", unique = true, nullable = false)
    private int numero;

   @Column(name="capacidad_carpa", nullable = false)
    private int pasillo;

   @Column(name="capacidad_carpa", nullable = false)
    private int capacidad;

}
