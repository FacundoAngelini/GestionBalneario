package com.Gestion.MiBalnearioGestion.Sector.Servicios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.DatosInvalidoException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EEstadoEmpleado;
import com.Gestion.MiBalnearioGestion.Sector.Entity.SectorEntity;
import com.Gestion.MiBalnearioGestion.Sector.DTO.SectorDTO;
import com.Gestion.MiBalnearioGestion.Sector.Mapper.SectorMapper;
import com.Gestion.MiBalnearioGestion.Sector.Repositorio.SectorRepositorio;
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
                .orElseThrow(() -> new EntidadNoEncontradaException("Sector no encontrado: " + publicId.toString(), "SectorEntity"));
    }

    @Transactional
    @Override
    public SectorDTO crearSector(SectorDTO dto) {
        if (sectorRepositorio.findByNombreIgnoreCase(dto.getNombre()).isPresent())
            throw new EntidadExistenteException("Ya existe un sector con ese nombre" + dto.getNombre(), "SectorEntity");
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
                .orElseThrow(() -> new EntidadNoEncontradaException("Sector no encontrado: "+ publicId.toString(), "SectorEntity"));

        sectorRepositorio.findByNombreIgnoreCase(dto.getNombre())
                .filter(s -> !s.getPublicId().equals(publicId))
                .ifPresent(s -> { throw new EntidadExistenteException(
                        "Ya existe un sector con ese nombre" + dto.getNombre(), "SectorEntity"); });

        sectorMapper.updateEntityFromDTO(dto, sector);
        return sectorMapper.convertToDTO(sectorRepositorio.save(sector));
    }

    @Transactional
    @Override
    public void borrarSector(UUID publicId) {
        SectorEntity sector = sectorRepositorio.findByPublicId(publicId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Sector no encontrado: "+ publicId.toString(),"SectorEntity"));

        boolean tieneEmpleadosActivos = sector.getEmpleados() != null &&
                sector.getEmpleados().stream()
                        .anyMatch(e -> e.getEstadoEmpleado() == EEstadoEmpleado.ACTIVO);

        if (tieneEmpleadosActivos)
            throw new DatosInvalidoException("No se puede eliminar el sector porque tiene empleados activos. " +
                    "De de baja los empleados primero", "SectorEntity");

        sectorRepositorio.delete(sector);
    }
}