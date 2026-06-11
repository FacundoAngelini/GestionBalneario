package com.Gestion.MiBalnearioGestion.Recursos.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RecursoResponseDTO {
    private UUID publicId;
    private String nombre;
    private boolean esReservable;
    private UUID sectorPublicId;
    private String sectorNombre;
    private List<PrecioRecursoResponseDTO> precios;
}
