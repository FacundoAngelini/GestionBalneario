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
public class CarpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
