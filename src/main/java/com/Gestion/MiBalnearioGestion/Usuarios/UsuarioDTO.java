package com.Gestion.MiBalnearioGestion.Usuarios;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
    public class UsuarioDTO {
    private UUID clienteId; // Mismo nombre que en la entidad para facilitarle la vida a ModelMapper
    private String nombreUsuario; // Lo vamos a traer desde la Credencial asociada
    private Set<String> roles;
  /*  @NotBlank
    private String nombreUsuario;
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contrasenia;
*/


}
