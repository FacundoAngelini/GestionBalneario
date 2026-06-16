package com.Gestion.MiBalnearioGestion.Pagos.DTOs;

import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Pagos.Entity.PagoEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DTO que representa el comprobante fiscal o ticket comercial emitido por el establecimiento")
public class TicketDTO {

    @Schema(description = "UUID público único del ticket emitido", example = "9f8e7d6c-5b4a-3f2e-1d0c-b1a09f8e7d6c")
    private UUID publicId;

    @Schema(description = "Fecha y hora exacta de emisión del ticket. Se genera automáticamente.", example = "2026-06-16T14:32:00")
    @Builder.Default
    private LocalDateTime fechaTicket = LocalDateTime.now();

    @Schema(description = "Monto total bruto facturado en el ticket (Debe ser mayor o igual a 0)", example = "15800.00")
    @NotNull
    @PositiveOrZero
    private Double total;

    @Schema(description = "UUID público del registro de pago asociado (null si el ticket aún está pendiente de cobro)", example = "5f4e3d2c-b1a0-9e8d-7c6b-5a4f3e2d1c0b", nullable = true)
    private UUID pagoPublicId;

    @Schema(description = "UUID público del empleado (cajero o mozo) que emitió o cerró el ticket", example = "3b2c1d0a-4e5f-6a7b-8c9d-0e1f2a3b4c5d")
    private UUID empleadoPublicId;

    @Schema(description = "UUID público del pedido o consumo que dio origen a la facturación de este ticket", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
    private UUID pedidoPublicId;
}