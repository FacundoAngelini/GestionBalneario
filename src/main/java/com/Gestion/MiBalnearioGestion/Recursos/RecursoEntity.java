package com.Gestion.MiBalnearioGestion.Recursos;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="Recurso")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecursoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "nombre_recurso", unique = true, nullable = false)
    private String nombre;


    private boolean esReservable;

    private String descripcion;
}
