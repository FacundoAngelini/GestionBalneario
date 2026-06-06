package com.Gestion.MiBalnearioGestion.Recursos.DTO;

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
public class PiletaDTO extends RecursoDTO{
    @NotNull
    private boolean esClimatizada;

    @NotNull
    private int tamanio;
}
