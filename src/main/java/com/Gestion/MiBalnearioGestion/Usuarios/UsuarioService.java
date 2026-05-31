package com.Gestion.MiBalnearioGestion.Usuarios;

import com.Gestion.MiBalnearioGestion.Auth.NewAccountRequest;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadNoEncontradaException;
import com.Gestion.MiBalnearioGestion.Common.Exepciones.EntidadExistenteException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService{

    private final UsuarioRepository usuarioRepositorio;
    private final UsuarioMapper usuarioMapper;

    @Override
    public List<UsuarioDTO> buscarTodosUsuarios(){
        return usuarioRepositorio.findAll().
                stream().
                map(usuarioMapper::convertToDTO)
                .toList();
    }

    @Override
    public UsuarioDTO buscarPorIdPublica(UUID idPublica){
        return usuarioRepositorio.
                findByPublicId(idPublica)
                .map(usuarioMapper::convertToDTO)
                .orElseThrow(()->new EntidadNoEncontradaException("No se encontro un usuario con esa id", "UsuarioEntity"));
    }


    @Transactional
    @Override
    public UsuarioDTO actualizarUsuario (UUID idPublica, UsuarioDTO dtoUsuario){
        UsuarioEntity usuario = usuarioRepositorio
                .findByPublicId(idPublica)
                .orElseThrow(()-> new EntidadNoEncontradaException("No se encontro un usuario con ese id", "UsuarioEntity"));

        if(usuarioRepositorio.findByNombreUsuario(dtoUsuario.getNombreUsuario()).isPresent()){
            throw new EntidadExistenteException("Ya existe un usuario con ese nombre", "UsuarioEntity");
        }

        return usuarioMapper.convertToDTO(usuarioRepositorio.save(usuario));
    }

    @Transactional
    @Override
    public void borrarUsuario (UUID idPublica){
        UsuarioEntity usuario = usuarioRepositorio
                .findByPublicId(idPublica)
                .orElseThrow(()-> new EntidadNoEncontradaException("No se encontro un usuario con ese id", "UsuarioEntity"));

        usuarioRepositorio.delete(usuario); // tendria que tener un estado y que se camie a inactivo
    }

}
