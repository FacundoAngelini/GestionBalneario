package com.Gestion.MiBalnearioGestion.Recursos.DTO.Response;

import com.Gestion.MiBalnearioGestion.Recursos.Enum.EtamanioSombrilla;
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
public class SombrillaResponseDTO extends RecursoResponseDTO{
    private int numero;
    private EtamanioSombrilla etamanio;
}

