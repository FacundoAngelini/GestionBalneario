package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoPedidoReservaDTO;

public interface IPagoPedidoReservaService {
    String iniciarPago(PagoPedidoReservaDTO dto);
}
