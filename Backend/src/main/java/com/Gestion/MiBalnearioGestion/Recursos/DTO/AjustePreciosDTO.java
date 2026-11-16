package com.Gestion.MiBalnearioGestion.Recursos.DTO;

import com.Gestion.MiBalnearioGestion.Recursos.Enum.EModalidad;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.EOperacionAjuste;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.ETipoRecurso;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud de ajuste masivo de precios de recursos")
public class AjustePreciosDTO {

    @Schema(description = "Tipo de recurso al que se aplica el ajuste", example = "CARPA")
    @NotNull
    private ETipoRecurso tipo;

    @Schema(description = "Limita el ajuste a un sector (null = todos los sectores)")
    private UUID sectorPublicId;

    @Schema(description = "Limita el ajuste a una modalidad (null = todas)", example = "DIA")
    private EModalidad modalidad;

    @Schema(description = "Cómo se calcula el precio nuevo", example = "PORCENTAJE")
    @NotNull
    private EOperacionAjuste operacion;

    @Schema(description = "Porcentaje (puede ser negativo) o monto fijo, según la operación", example = "10.0")
    @NotNull
    private Double valor;

    @Schema(description = "Si es true solo calcula y devuelve el resultado, sin guardar nada", example = "true")
    private boolean soloSimular;
}