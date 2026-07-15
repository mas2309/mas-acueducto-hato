package com.mas.co.service.impl;

import com.mas.co.dto.UsuarioDto;
import com.mas.co.entity.Usuario;
import com.mas.co.exception.BusinessException;
import com.mas.co.mapper.UsuarioMapper;
import com.mas.co.repository.UsuarioRepository;
import com.mas.co.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de Usuario.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    @Transactional
    public UsuarioDto crearUsuario(UsuarioDto usuarioDto) {
        log.debug("Creando usuario: {}", usuarioDto.getNombre());
        
        Usuario usuario = usuarioMapper.toEntity(usuarioDto);
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        log.info("Usuario creado exitosamente con ID: {}", usuarioGuardado.getId());
        return usuarioMapper.toDto(usuarioGuardado);
    }

    @Override
    public UsuarioDto obtenerUsuario(Long id) {
        log.debug("Obteniendo usuario con ID: {}", id);
        
        Usuario usuario = usuarioRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> BusinessException.userNotFound(id));
        
        return usuarioMapper.toDto(usuario);
    }

    @Override
    @Transactional
    public UsuarioDto actualizarUsuario(Long id, UsuarioDto usuarioDto) {
        log.debug("Actualizando usuario con ID: {}", id);
        
        Usuario usuario = usuarioRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> BusinessException.userNotFound(id));
        
        usuarioMapper.updateEntity(usuarioDto, usuario);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        
        log.info("Usuario actualizado exitosamente: {}", id);
        return usuarioMapper.toDto(usuarioActualizado);
    }

    @Override
    @Transactional
    public void desactivarUsuario(Long id) {
        log.debug("Desactivando usuario con ID: {}", id);
        
        Usuario usuario = usuarioRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> BusinessException.userNotFound(id));
        
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        
        log.info("Usuario desactivado exitosamente: {}", id);
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        log.debug("Eliminando usuario con ID: {}", id);
        
        if (!usuarioRepository.existsById(id)) {
            throw BusinessException.userNotFound(id);
        }
        
        usuarioRepository.deleteById(id);
        
        log.info("Usuario eliminado exitosamente: {}", id);
    }

    @Override
    public Page<UsuarioDto> obtenerUsuarios(Pageable pageable) {
        log.debug("Obteniendo usuarios paginados: página {}, tamaño {}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Usuario> usuarios = usuarioRepository.findByActivoTrue(pageable);
        return usuarios.map(usuarioMapper::toDto);
    }

    @Override
    public Page<UsuarioDto> buscarUsuarios(String termino, Pageable pageable) {
        log.debug("Buscando usuarios con término: {}", termino);
        
        Page<Usuario> usuarios = usuarioRepository.findByNombreOrApellidosContainingIgnoreCase(
                termino, termino, pageable);
        return usuarios.map(usuarioMapper::toDto);
    }

    @Override
    public List<UsuarioDto> obtenerUsuariosActivos() {
        log.debug("Obteniendo todos los usuarios activos");
        
        List<Usuario> usuarios = usuarioRepository.findByActivoTrueOrderByNombreAsc();
        return usuarioMapper.toDtoList(usuarios);
    }
}