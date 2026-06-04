package com.Gestion.MiBalnearioGestion.Recursos.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RecursoDTO {

    private UUID publicID;

    @NotBlank
    private String nombre;

    @NotNull
    private boolean esReservable;

    @NotNull
    private UUID sectorPublicId;



}
