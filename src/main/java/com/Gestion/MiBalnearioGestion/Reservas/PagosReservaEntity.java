package com.Gestion.MiBalnearioGestion.Reservas;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name="pagos_reserva")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagosReservaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "pago_reserva")
    private ReservaEntity reserva;

}
