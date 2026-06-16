package com.Gestion.MiBalnearioGestion.Reservas.Servicio;

import com.Gestion.MiBalnearioGestion.Reservas.DTO.CancelarReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.CheckoutResponseDTO;
import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.EReservaEstado;
import com.Gestion.MiBalnearioGestion.Reservas.Entity.ReservaEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IReservaServicio {
    ReservaEntity crearReservaInicial(ReservaDTO dto);
    CheckoutResponseDTO crearReservaYGenerarCheckout(ReservaDTO dto);
    ReservaDTO buscarPorPublicId(UUID publicId);
    void cancelarReservaConAnticipacion(CancelarReservaDTO dto);
    List<ReservaDTO> listarReservasConFiltros(EReservaEstado estado,
                                              LocalDate fechaDesde,
                                              LocalDate fechaHasta,
                                              UUID clientePublicId);
    void cancelarReservaPorPersonal(UUID reservaPublicId);
    void cancelarReservaPorExpiracion(UUID publicId);
}
