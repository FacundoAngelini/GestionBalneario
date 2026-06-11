package com.Gestion.MiBalnearioGestion.Recursos.DTO.Response;

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
public class CocheraResponseDTO extends RecursoResponseDTO {
    private int numeroCochera;
}
