package com.Gestion.MiBalnearioGestion.Pedidos;

import com.Gestion.MiBalnearioGestion.Empleados.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Pagos.PagoEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
