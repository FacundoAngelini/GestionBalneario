package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoEntity;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;
import java.util.UUID;

public class TicketDTO {

    @NotNull
    private UUID publicId;

    @NotNull
    @PastOrPresent
    private LocalDateTime fechaTicket;

    @NotNull
    @PositiveOrZero
    private Double total;

    @NotNull
    private PagoDTO pagoDTO;

    @NotNull
    private EmpleadoDTO empleadoDTO;
}
