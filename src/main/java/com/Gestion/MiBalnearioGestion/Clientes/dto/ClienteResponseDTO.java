package com.Gestion.MiBalnearioGestion.Clientes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResponseDTO {
    private UUID publicId;
    private String nombre;
    private String apellido;
    private Integer dni;
    private String email;
    private String telefono;
    private LocalDate fechaAlta;
    private boolean estado;
}