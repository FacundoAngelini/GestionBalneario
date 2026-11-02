package com.Gestion.MiBalnearioGestion.Pedidos.Entity;

import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name="entregas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", unique = true, nullable = false, updatable = false)
    @UuidGenerator
    private UUID publicId;

    @Column(name = "estado_entrega", nullable = false)
    private boolean estadoEntrega;

    @OneToOne
    @JoinColumn(name = "pedido_reserva_id", nullable = false)
    private PedidoLugarEntity pedidoReserva;  // corregido

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private EmpleadoEntity empleado;
}
