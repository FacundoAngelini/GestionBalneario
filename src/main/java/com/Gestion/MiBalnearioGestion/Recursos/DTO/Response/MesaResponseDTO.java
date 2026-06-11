package com.Gestion.MiBalnearioGestion.Recursos.DTO.Response;

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
public class MesaResponseDTO extends RecursoResponseDTO{
    private int numero;
    private int capacidad;
}
