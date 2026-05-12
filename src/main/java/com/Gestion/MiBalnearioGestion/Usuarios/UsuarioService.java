package com.Gestion.MiBalnearioGestion.Usuarios;

import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.ExEntidadExistente;
import com.Gestion.MiBalnearioGestion.Empleados.DTO.EmpleadoDTO;
import com.Gestion.MiBalnearioGestion.Empleados.EEstadoEmpleado;
import com.Gestion.MiBalnearioGestion.Empleados.Entities.EmpleadoEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService{

    private final UsuarioRepository usuarioRepositorio;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    @Override
    public UsuarioDTO crearUsuario (UsuarioDTO dtoUsuario){

        if (usuarioRepositorio.findByUsuario(dtoUsuario.getNombreUsuario()).isPresent()){
            throw new ExEntidadExistente("Ya existe un usuario con ese DNI", "UsuarioEntity");
        }

        UsuarioEntity usuario = usuarioMapper.convertToEntity(dtoUsuario, UsuarioEntity.class);
        UsuarioEntity usuarioGuardado = usuarioRepositorio.save(usuario);

        return usuarioMapper.convertToDTO(usuarioGuardado);
    }

    @Override
    public List<UsuarioDTO> listarUsuarios(){
        return usuarioRepositorio.findAll().
                stream().
                map(usuarioMapper::convertToDTO)
                .toList();
    }

    @Override
    public UsuarioDTO buscarPorIdPublica(UUID idPublica){
        return usuarioRepositorio.
                findByIdPublica(idPublica)
                .map(usuarioMapper::convertToDTO)
                .orElseThrow(()->new EntidadNoEncontradaException("No se encontro un usuario con esa id", "UsuarioEntity"));
    }

    @Transactional
    @Override
    public UsuarioDTO actualizarUsuario (UUID idPublica, UsuarioDTO dtoUsuario){
        UsuarioEntity usuario = usuarioRepositorio
                .findByIdPublica(idPublica)
                .orElseThrow(()-> new EntidadNoEncontradaException("No se encontro un usuario con ese id", "UsuarioEntity"));

        if(usuarioRepositorio.findByUsuario(dtoUsuario.getNombreUsuario()).isPresent()){
            throw new ExEntidadExistente("Ya existe un usuario con ese nombre", "UsuarioEntity");
        }

        return usuarioMapper.convertToDTO(usuarioRepositorio.save(usuario));
    }

    @Transactional
    @Override
    public void BorrarUsuario (UUID idPublica){
        UsuarioEntity usuario = usuarioRepositorio
                .findByIdPublica(idPublica)
                .orElseThrow(()-> new EntidadNoEncontradaException("No se encontro un usuario con ese id", "UsuarioEntity"));

        usuarioRepositorio.delete(usuario); // tendria que tener un estado y que se camie a inactivo
    }

}
