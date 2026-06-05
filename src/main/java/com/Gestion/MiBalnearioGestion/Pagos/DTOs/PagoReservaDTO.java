package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Reservas.ReservaDTO;
import jakarta.validation.constraints.NotNull;

public class PagoReservaDTO {

    @NotNull
    private ReservaDTO reservaDTO;

}
