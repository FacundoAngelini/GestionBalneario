package com.Gestion.MiBalnearioGestion.Reservas.Entity;

import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoReservaEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoReservaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.RecursoEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private UUID publicId;

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

    @Column(name = "disponible_desde")
    private LocalDate disponibleDesde;

    @Column(name = "disponible_hasta")
    private LocalDate disponibleHasta;

    @ManyToOne
    @JoinColumn(name="cliente_id", nullable = false)
    private ClienteEntity cliente;

    @ManyToMany
    @JoinTable(
            name="reserva_recurso",
            joinColumns = @JoinColumn (name="reserva_id"),
            inverseJoinColumns = @JoinColumn(name="recurso_id")
    )
    @JsonManagedReference
    private List<RecursoEntity> recursos = new ArrayList<>();

    @OneToMany(mappedBy = "reserva")
    private List<PedidoReservaEntity> pedidoReserva;

    @OneToOne(mappedBy = "reserva")
    @JsonManagedReference
    private PagoReservaEntity pagosReservaaa;
}
