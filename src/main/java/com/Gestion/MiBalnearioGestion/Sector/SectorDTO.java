package com.Gestion.MiBalnearioGestion.Sector;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorDTO {
    private UUID publicId;
    @NotNull
    @NotBlank
    private String nombre;

}
