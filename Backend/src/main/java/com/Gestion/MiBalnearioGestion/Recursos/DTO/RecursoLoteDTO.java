package com.Gestion.MiBalnearioGestion.Recursos.DTO;

import com.Gestion.MiBalnearioGestion.Recursos.Enum.EModalidad;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.ETipoRecurso;
import com.Gestion.MiBalnearioGestion.Recursos.Enum.EtamanioSombrilla;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Datos para la creación en lote de recursos (carpas, sombrillas, cocheras)")
@Getter
@Setter
public class RecursoLoteDTO {

    @Schema(description = "Tipo de recurso a crear", example = "CARPA")
    @NotNull
    private ETipoRecurso tipo;

    @Schema(description = "Prefijo para el nombre de los recursos", example = "Carpa")
    @NotBlank
    private String prefijoNombre;

    @Schema(description = "Número inicial del lote", example = "1")
    @NotNull @Min(1)
    private Integer numeroDesde;

    @Schema(description = "Número final del lote", example = "10")
    @NotNull @Min(1)
    private Integer numeroHasta;

    @Schema(description = "Identificador público del sector al que pertenecen", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull
    private UUID sectorPublicId;

    @Schema(description = "Número de pasillo (Aplica solo para CARPA)", example = "2")
    private Integer pasillo;

    @Schema(description = "Capacidad máxima de personas (Aplica solo para CARPA)", example = "6")
    private Integer capacidad;

    @Schema(description = "Tamaño de la sombrilla (Aplica solo para SOMBRILLA)", example = "GRANDE")
    private EtamanioSombrilla tamanio;

    @Schema(description = "Lista de precios asociados a este lote de recursos")
    @NotEmpty
    @Valid
    private List<PrecioLoteDTO> precios;
}
