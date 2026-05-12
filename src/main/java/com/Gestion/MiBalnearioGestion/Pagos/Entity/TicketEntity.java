package com.Gestion.MiBalnearioGestion.Pagos.Entity;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="public_id", unique=true, nullable=false, updatable = false)
    @UuidGenerator
    private UUID publicId;

    @Column(name="fecha_ticket", nullable = false)
    private LocalDateTime fechaTicket;

    @Column(name="total", nullable = false)
    private Double total;

    @OneToOne
    @JoinColumn(name="pagos_id", nullable = false)
    private PagoEntity pagoEntity;

    @ManyToOne
    @JoinColumn(name="empleado_id", nullable = false)
    private EmpleadoEntity empleado;
}
