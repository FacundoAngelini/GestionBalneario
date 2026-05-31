package com.Gestion.MiBalnearioGestion.Usuarios;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
    public class UsuarioDTO {
    @NotBlank
    private String nombreUsuario;
    @NotBlank
    @JsonIgnore
    private String contrasenia;

}
