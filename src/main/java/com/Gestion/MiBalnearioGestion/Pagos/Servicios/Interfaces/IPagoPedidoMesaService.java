package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoMesaDTO;

public interface IPagoPedidoMesaService {
    String iniciarPago(PagoPedidoMesaDTO dto);
}
