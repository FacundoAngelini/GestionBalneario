package com.Gestion.MiBalnearioGestion.Pagos.Entity;

import jakarta.persistence.*;
import lombok.*;

/** Una única fila con los parámetros de cobro. */
@Entity
@Table(name = "configuracion_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionPagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Porcentaje de descuento para pagos en efectivo (0 = sin descuento). */
    @Column(name = "descuento_efectivo", nullable = false)
    private double descuentoEfectivo;
}