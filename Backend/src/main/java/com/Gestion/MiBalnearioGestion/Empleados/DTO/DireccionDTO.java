package com.Gestion.MiBalnearioGestion.Empleados.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO que representa la estructura de una dirección postal o localización geográfica física")
public class DireccionDTO {

    @Schema(description = "Nombre de la calle o avenida", example = "Av. Colón")
    @NotBlank(message = "La calle es obligatoria")
    @Size(max=25, message = "La calle no puede contener mas de 25 caracteres")
    private String calle;

    @Schema(description = "Altura catastral o número domiciliario (Debe ser positivo o cero)", example = "3450")
    @NotNull(message = "El número es obligatorio")
    @PositiveOrZero(message = "El numero debe ser positivo o cero")
    @Max(value = 15000, message = "El numero no puede ser mayor de 15000")
    private Integer numero;

    @Schema(description = "Nombre de la localidad o ciudad", example = "Mar del Plata")
    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max=25, message = "La ciudad no puede contener mas de 25 caracteres")
    private String ciudad;

    @Schema(description = "Nombre de la provincia, estado o región administrativa", example = "Buenos Aires")
    @NotBlank(message = "La provincia es obligatoria")
    @Size(max=25, message = "La provincia no puede contener mas de 25 caracteres")
    private String provincia;
}