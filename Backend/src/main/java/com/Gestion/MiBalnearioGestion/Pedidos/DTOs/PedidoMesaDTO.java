package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoMesaDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoMesaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.MesaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.MesaEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Schema(description = "DTO de solicitud para registrar un pedido asociado directamente a una mesa del restaurante o bar")
public class PedidoMesaDTO extends PedidoRequest {

    @Schema(description = "UUID público de la mesa física donde se procesa el consumo", example = "d3b07384-d113-4c4e-9c8e-cfbd6c4e3012")
    @NotNull
    private UUID mesaId;
}