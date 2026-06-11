package com.Gestion.MiBalnearioGestion.Recursos.DTO.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CarpaRequestDTO extends RecursoRequestDTO {
    @NotNull @Min(1)
    private int numero;
    @NotNull @Min(1)
    private int pasillo;
    @NotNull @Min(2)
    private int capacidad;
}
