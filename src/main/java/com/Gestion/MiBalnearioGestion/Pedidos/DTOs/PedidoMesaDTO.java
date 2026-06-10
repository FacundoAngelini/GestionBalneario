package com.Gestion.MiBalnearioGestion.Pedidos.DTOs;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoMesaDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoPedidoMesaEntity;
import com.Gestion.MiBalnearioGestion.Recursos.Entity.MesaEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

public class PedidoMesaDTO {

    //@NotNull
    //private MesaDTO mesa;

    @NotNull
    private PagoPedidoMesaDTO pagoPedidoMesaDTO;

}
