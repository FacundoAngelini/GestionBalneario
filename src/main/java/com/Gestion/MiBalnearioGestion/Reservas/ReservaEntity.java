package com.Gestion.MiBalnearioGestion.Reservas;

import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Pagos.PagoReservaEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.PedidoReservaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.RecursoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="public_id", unique=true, nullable=false, updatable = false)
    @UuidGenerator
    private String publicId;

    @Column(name="fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name="fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name="reservado", nullable = false)
    private boolean reservado;

    @Enumerated(EnumType.STRING)
    @Column(name="estado_reserva", nullable = false)
    private EReservaEstado estadoReserva;

    @Column(name="monto_total", nullable = false)
    private double montoTotal;

    @ManyToOne
    @JoinColumn(name="cliente_id", unique = true, nullable = false)
    private ClienteEntity cliente;

    @ManyToMany
    @JoinTable(
            name="reserva_recurso",// tabla intermeedia
            joinColumns = @JoinColumn (name="reserva_id"),
            inverseJoinColumns = @JoinColumn(name="recurso_id")
    )
    private List<RecursoEntity> recursos=new ArrayList<>();

    @OneToMany(mappedBy = "reserva")
    private List<PedidoReservaEntity> pedidoReserva;

    @OneToOne(mappedBy = "reserva")
    private PagoReservaEntity pagosReservaaa;

}
