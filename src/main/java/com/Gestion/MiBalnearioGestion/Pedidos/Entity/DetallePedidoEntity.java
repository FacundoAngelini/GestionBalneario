package com.Gestion.MiBalnearioGestion.Pedidos.Entity;

import com.Gestion.MiBalnearioGestion.Productos.ProductoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="detalle_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedidoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="public_id", unique=true, nullable=false, updatable = false)
    @UuidGenerator
    private UUID publicId;

    @Column(name="cantidad", nullable = false)
    private int cantidad;

    @Column(name="precio", nullable = false)
    private double precio;

    @ManyToMany
    @JoinTable(
            name="detalle_productos",
            joinColumns = @JoinColumn(name="detalle_id"),
            inverseJoinColumns = @JoinColumn(name="producto_id")
    )
    private List<ProductoEntity> productos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="pedido_id", nullable = false)
    private PedidoEntity pedido;
}
