package com.Gestion.MiBalnearioGestion.Pedidos;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
