package com.Gestion.MiBalnearioGestion.Recursos.DTO;

import com.Gestion.MiBalnearioGestion.Recursos.Enum.EModalidad;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Schema(description = "Representación de un recurso en el catálogo público o panel")
@Getter
@Builder
public class RecursoCatalogoDTO {
    @Schema(description = "UUID público del recurso")
    private UUID publicId;
    @Schema(description = "Nombre generado", example = "Carpa 4")
    private String nombre;
    @Schema(description = "Tipo de recurso", example = "CARPA")
    private String tipo;
    @Schema(description = "UUID del sector")
    private UUID sectorPublicId;
    @Schema(description = "Nombre del sector", example = "Sector VIP")
    private String sectorNombre;
    @Schema(example = "4")
    private Integer numero;
    @Schema(example = "2")
    private Integer pasillo;
    @Schema(example = "6")
    private Integer capacidad;
    @Schema(description = "Aplica a sombrillas", example = "CHICA")
    private String tamanio;

    private List<PrecioCatalogoDTO> precios;

    @Schema(description = "Precio específico dentro del catálogo")
    public record PrecioCatalogoDTO(
            @Schema(example = "QUINCENA") EModalidad modalidad,
            @Schema(example = "75000.0") double precio,
            LocalDate fechaVigencia,
            LocalDate fechaCaducada) {}
}