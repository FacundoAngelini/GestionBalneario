package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoMesaDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoMesaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.DTO.MesaDTO;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.MesaEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class PedidoMesaDTO {

    @NotNull
    private UUID MesaId;

    @NotNull
    private PagoPedidoMesaDTO pagoPedidoMesaDTO;

}
