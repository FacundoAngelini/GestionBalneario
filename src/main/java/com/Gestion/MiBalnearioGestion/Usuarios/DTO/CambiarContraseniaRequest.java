package com.Gestion.MiBalnearioGestion.Usuarios.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CambiarContraseniaRequest {
    @NotBlank
    private String contraseniaActual;
    @NotBlank
    private String contraseniaNueva;
}
