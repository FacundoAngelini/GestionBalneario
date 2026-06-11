package com.Gestion.MiBalnearioGestion.Recursos.DTO.Response;

import com.Gestion.MiBalnearioGestion.Recursos.Enum.ETipoCancha;
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
public class CanchaResponseDTO extends RecursoResponseDTO {
    private ETipoCancha tipoCancha;
    private int capacidad;
    private boolean iluminacion;
}
