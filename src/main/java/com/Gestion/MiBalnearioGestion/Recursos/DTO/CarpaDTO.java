package com.Gestion.MiBalnearioGestion.Recursos.DTO;

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
public class CarpaDTO extends RecursoDTO{
    @NotNull
    @Min(1)
    private Integer numero;
    @NotNull
    @Min(1)
    private Integer pasillo;
    @NotNull
    @Min(2)
    private Integer capacidad;
}
