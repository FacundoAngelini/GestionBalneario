package com.Gestion.MiBalnearioGestion.Recursos.DTO;

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
public class PiletaDTO extends RecursoDTO{
    @NotNull
    private Boolean esClimatizada;

    @NotNull
    private Integer tamanio;
}
