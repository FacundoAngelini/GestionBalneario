package com.Gestion.MiBalnearioGestion.Clientes.dto;

import java.util.UUID;

/** Datos mínimos para identificar a un cliente en el mostrador (sin email ni teléfono). */
public record ClienteBusquedaDTO(UUID publicId, String nombre, String apellido, Integer dni, boolean activo) {}