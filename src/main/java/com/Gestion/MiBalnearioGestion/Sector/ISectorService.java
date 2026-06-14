package com.Gestion.MiBalnearioGestion.Sector;

import java.util.List;
import java.util.UUID;

public interface ISectorService {
    List<SectorDTO> listarTodos();
    SectorDTO buscarPorId(UUID publicId);
    SectorDTO crearSector(SectorDTO dto);
    SectorDTO actualizarSector(UUID publicId, SectorDTO dto);
    void borrarSector(UUID publicId);
}
