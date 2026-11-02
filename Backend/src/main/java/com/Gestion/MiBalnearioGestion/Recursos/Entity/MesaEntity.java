package com.Gestion.MiBalnearioGestion.Recursos.Entity;

import com.Gestion.MiBalnearioGestion.Pedidos.Entity.PedidoMesaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Table(name="Mesas")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MesaEntity extends RecursoEntity {
    @Column(name="numero_mesa", unique = true, nullable = false)
    private int numero;

    @Column(name="capacidad_mesa", nullable = false)
    private int capacidad;

    @OneToMany(mappedBy = "mesa")
    private List<PedidoMesaEntity> pedidos = new ArrayList<>();
}
