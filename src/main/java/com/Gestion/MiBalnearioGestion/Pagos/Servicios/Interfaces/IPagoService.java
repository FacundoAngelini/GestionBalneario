package com.Gestion.MiBalnearioGestion.Pagos.Servicios.Interfaces;

import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoDTO;
import com.Gestion.MiBalnearioGestion.Pagos.DTOs.PagoReservaResponseDTO;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.EestadoPago;
import com.Gestion.MiBalnearioGestion.Pagos.Enum.MetodoPago;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IPagoService {
   void procesarNotificacionPago(String paymentIdMP);
    PagoReservaResponseDTO obtenerPagoPorReserva(UUID reservaPublicId);
    List<PagoReservaResponseDTO> buscarPagosConFiltros(EestadoPago estado,
                                                       MetodoPago metodo,
                                                       Double montoMin,
                                                       Double montoMax,
                                                       LocalDate fechaDesde,
                                                       LocalDate fechaHasta);
    void cancelarPagoYReserva(UUID reservaPublicId);
    PagoDTO obtenerPagoPorPedido(UUID pedidoPublicId);

}
