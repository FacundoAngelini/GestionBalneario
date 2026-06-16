package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Reservas.DTO.ReservaDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de solicitud/respuesta para registrar el pago correspondiente al alquiler o reserva de un recurso del establecimiento")
public class PagoReservaDTO extends PagoDTO {

    @Schema(description = "Información detallada del contrato de reserva, fechas de estadía y cliente asociado al alquiler")
    @NotNull
    private ReservaDTO reservaDTO;
}