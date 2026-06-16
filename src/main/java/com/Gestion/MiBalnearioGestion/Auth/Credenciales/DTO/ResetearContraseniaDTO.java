package com.Gestion.MiBalnearioGestion.Auth.Credenciales.DTO;

import lombok.Builder;

@Builder
public record ResetearContraseniaDTO( String token,
                                      String nuevaContrasenia) {
}
