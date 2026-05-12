package com.Gestion.MiBalnearioGestion.Pagos.Entity;

import com.Gestion.MiBalnearioGestion.Pagos.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.MetodoPago;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

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

    @Column(name="public_id", unique=true, nullable=false, updatable = false)
    @UuidGenerator
    private UUID publicId;

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

    @OneToOne(mappedBy = "pagoEntity")
    private TicketEntity ticket;


}
