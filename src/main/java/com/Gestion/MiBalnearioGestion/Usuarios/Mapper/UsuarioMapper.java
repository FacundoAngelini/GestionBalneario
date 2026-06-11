package com.Gestion.MiBalnearioGestion.Usuarios.Mapper;
import com.Gestion.MiBalnearioGestion.Clientes.ClienteEntity;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteDTO;
import com.Gestion.MiBalnearioGestion.Clientes.dto.ClienteResponseDTO;
import com.Gestion.MiBalnearioGestion.Common.Model.IMapper;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.DireccionDTO;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.RolDTO;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.SectorDTO;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import com.Gestion.MiBalnearioGestion.Usuarios.DTO.UsuarioDTO;
import com.Gestion.MiBalnearioGestion.Usuarios.UsuarioEntity;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
@Component
public class UsuarioMapper {


        // Sin ModelMapper — todo a mano para evitar conflictos de typeMap compartido
        // y dependencias circulares entre entidades relacionadas bidirecionalmente

        public UsuarioDTO convertToDTO(UsuarioEntity entity) {
            if (entity == null) return null;

            UsuarioDTO dto = new UsuarioDTO();
            dto.setPublicId(entity.getPublicId());
            dto.setActivo(entity.isActivo());

            if (entity.getCredencial() != null) {
                dto.setNombreUsuario(entity.getCredencial().getNombreUsuario());
            }

            if (entity.getCliente() != null) {
                dto.setCliente(mapCliente(entity.getCliente()));
            }

            if (entity.getEmpleado() != null) {
                dto.setEmpleado(mapEmpleado(entity.getEmpleado()));
            }

            return dto;
        }

        private ClienteResponseDTO mapCliente(ClienteEntity cliente) {
            ClienteResponseDTO dto = new ClienteResponseDTO();
            dto.setPublicId(cliente.getPublicId());
            dto.setNombre(cliente.getNombre());
            dto.setApellido(cliente.getApellido());
            dto.setDni(cliente.getDni());
            dto.setEmail(cliente.getEmail());
            dto.setTelefono(cliente.getTelefono());
            dto.setFechaAlta(cliente.getFecha_alta());
            dto.setEstado(cliente.isEstado());
            return dto;
        }

        private EmpleadoDTO mapEmpleado(EmpleadoEntity empleado) {
            EmpleadoDTO dto = new EmpleadoDTO();
            dto.setIDpublico(empleado.getPublicId());
            dto.setNombre(empleado.getNombre());
            dto.setApellido(empleado.getApellido());
            dto.setDni(empleado.getDni());
            dto.setEmail(empleado.getEmail());
            dto.setSueldo(empleado.getSueldo());
            dto.setCuit(empleado.getCuit());
            dto.setEstado(empleado.getEstadoEmpleado());
            dto.setTelefono(empleado.getTelefono());

            if (empleado.getUsuario() != null) {
                dto.setUsuarioPublicId(empleado.getUsuario().getPublicId());

                if (empleado.getUsuario().getCredencial() != null) {
                    dto.setNombreUsuario(empleado.getUsuario().getCredencial().getNombreUsuario());
                }
            }

            if (empleado.getDireccion() != null) {
                DireccionDTO dir = new DireccionDTO();
                dir.setCalle(empleado.getDireccion().getCalle());
                dir.setNumero(empleado.getDireccion().getNumero());
                dir.setCiudad(empleado.getDireccion().getCiudad());
                dir.setProvincia(empleado.getDireccion().getProvincia());
                dto.setDireccion(dir);
            }

            if (empleado.getSector() != null) {
                SectorDTO sector = new SectorDTO();
                sector.setPublicId(empleado.getSector().getPublicId());
                sector.setNombre(empleado.getSector().getNombre());
                dto.setSector(sector);
            }

            if (empleado.getRol() != null) {
                RolDTO rol = new RolDTO();
                rol.setPublicId(empleado.getRol().getPublicId());
                rol.setRol(empleado.getRol().getTipoRol());
                dto.setRol(rol);
            }

            return dto;
        }
    }
