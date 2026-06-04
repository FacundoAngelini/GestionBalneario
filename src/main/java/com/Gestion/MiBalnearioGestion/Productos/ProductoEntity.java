package com.Gestion.MiBalnearioGestion.Productos;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.DetallePedidoEntity;
import com.Gestion.MiBalnearioGestion.Pedidos.Enum.ECategoriaProdcuto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="public_id", unique=true, nullable=false, updatable = false)
    @UuidGenerator
    private UUID publicId;

    @Column(name="nombre", unique = true, nullable = false)
    private String nombre;

    @Column(name="precio", nullable = false)
    private double precio;

    @Enumerated(EnumType.STRING)
    @Column(name="categoria", nullable = false)
    private ECategoriaProdcuto categoria;

    @Column(name="producto_disponible", nullable = false)
    private boolean producto_disponible;

    @ManyToMany(mappedBy = "productos")
    private List<DetallePedidoEntity> detallePedidos = new ArrayList<>();
}
