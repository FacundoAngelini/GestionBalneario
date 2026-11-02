package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Pedidos.DTOs.PedidoMesaDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de solicitud para registrar el pago y cierre de una mesa del sector gastronómico")
public class PagoPedidoMesaDTO extends PagoDTO {

    @Schema(description = "Información detallada de la comanda y productos de la mesa que se están abonando")
    @NotNull
    private PedidoMesaDTO pedidoMesaDTO;
}