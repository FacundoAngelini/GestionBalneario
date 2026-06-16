package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DireccionDTO {
    @NotBlank(message = "La calle es obligatoria")
    private String calle;

    @NotNull(message = "El número es obligatorio")
    @PositiveOrZero(message = "El numero debe ser positivo o cero")
    private Integer numero;

    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    @NotBlank(message = "La provincia es obligatoria")
    private String provincia;
}
