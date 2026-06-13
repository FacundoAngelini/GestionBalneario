package com.Gestion.MiBalnearioGestion.Sector;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SectorService implements ISectorService {

    private final SectorRepositorio sectorRepositorio;
    private final SectorMapper sectorMapper;

    @Transactional(readOnly = true)
    @Override
    public List<SectorDTO> listarTodos() {
        return sectorRepositorio.findAll()
                .stream()
                .map(sectorMapper::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public SectorDTO buscarPorId(UUID publicId) {
        return sectorRepositorio.findByPublicId(publicId)
                .map(sectorMapper::convertToDTO)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Sector no encontrado: ", publicId.toString()));
    }

    @Transactional
    @Override
    public SectorDTO crearSector(SectorDTO dto) {
        if (sectorRepositorio.findByNombreIgnoreCase(dto.getNombre()).isPresent())
            throw new EntidadExistenteException(
                    "Ya existe un sector con ese nombre", "SectorEntity");

        SectorEntity nuevo = SectorEntity.builder()
                .nombre(dto.getNombre())
                .empleados(new ArrayList<>())
                .recursos(new ArrayList<>())
                .build();

        return sectorMapper.convertToDTO(sectorRepositorio.save(nuevo));
    }

    @Transactional
    @Override
    public SectorDTO actualizarSector(UUID publicId, SectorDTO dto) {
        SectorEntity sector = sectorRepositorio.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Sector no encontrado: ", publicId.toString()));

        sectorRepositorio.findByNombreIgnoreCase(dto.getNombre())
                .filter(s -> !s.getPublicId().equals(publicId))
                .ifPresent(s -> { throw new EntidadExistenteException(
                        "Ya existe un sector con ese nombre", "SectorEntity"); });

        sectorMapper.updateEntityFromDTO(dto, sector);
        return sectorMapper.convertToDTO(sectorRepositorio.save(sector));
    }

    @Transactional
    @Override
    public void borrarSector(UUID publicId) {
        SectorEntity sector = sectorRepositorio.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "Sector no encontrado: ", publicId.toString()));

        boolean tieneEmpleadosActivos = sector.getEmpleados() != null &&
                sector.getEmpleados().stream()
                        .anyMatch(e -> e.getEstadoEmpleado() == EEstadoEmpleado.ACTIVO);

        if (tieneEmpleadosActivos)
            throw new IllegalStateException(
                    "No se puede eliminar el sector porque tiene empleados activos. " +
                            "Dé de baja los empleados primero.");

        sectorRepositorio.delete(sector);
    }
}