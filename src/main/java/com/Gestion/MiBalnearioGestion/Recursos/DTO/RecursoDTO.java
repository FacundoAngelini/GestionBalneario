package com.Gestion.MiBalnearioGestion.Recursos.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class RecursoDTO {

    private UUID publicId;

    @NotBlank
    private String nombre;

    @NotNull
    private Boolean esReservable;

    @NotNull
    private UUID sectorPublicId;



}
