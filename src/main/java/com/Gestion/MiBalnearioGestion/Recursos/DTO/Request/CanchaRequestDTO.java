package com.Gestion.MiBalnearioGestion.Recursos.DTO.Request;

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
public class CanchaRequestDTO extends RecursoRequestDTO {

    @NotNull
    private ETipoCancha tipoCancha;

    @Min(2)
    @NotNull
    private int capacidad;

    @NotNull
    private boolean iluminacion;


}
