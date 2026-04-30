package com.Gestion.MiBalnearioGestion.Pagos;

import com.Gestion.MiBalnearioGestion.Pedidos.TicketEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name="pagos")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PagoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="monto", nullable = false)
    private double monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EestadoPago eestadoPago;

    @Column(name="fecha_pago", nullable = false)
    private LocalDate fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(name="metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Column(name="descuento", nullable = false)
    private double descuento;

    @OneToOne(mappedBy = "pagos")
    private TicketEntity ticket;


}
