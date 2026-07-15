package com.mas.co.mapper;

import com.mas.co.dto.UsuarioDto;
import com.mas.co.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre Usuario y UsuarioDto.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@Component
public class UsuarioMapper {

    /**
     * Convierte entidad a DTO.
     * 
     * @param usuario entidad
     * @return DTO
     */
    public UsuarioDto toDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        
        return UsuarioDto.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellidos(usuario.getApellidos())
                .fechaInsert(usuario.getFechaInsert())
                .activo(usuario.getActivo())
                .build();
    }

    /**
     * Convierte DTO a entidad.
     * 
     * @param dto DTO
     * @return entidad
     */
    public Usuario toEntity(UsuarioDto dto) {
        if (dto == null) {
            return null;
        }
        
        return Usuario.builder()
                .nombre(dto.getNombre())
                .apellidos(dto.getApellidos())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
    }

    /**
     * Actualiza entidad existente con datos del DTO.
     * 
     * @param dto DTO con nuevos datos
     * @param usuario entidad a actualizar
     */
    public void updateEntity(UsuarioDto dto, Usuario usuario) {
        if (dto == null || usuario == null) {
            return;
        }
        
        if (dto.getNombre() != null) {
            usuario.setNombre(dto.getNombre());
        }
        if (dto.getApellidos() != null) {
            usuario.setApellidos(dto.getApellidos());
        }
        if (dto.getActivo() != null) {
            usuario.setActivo(dto.getActivo());
        }
    }

    /**
     * Convierte lista de entidades a DTOs.
     * 
     * @param usuarios lista de entidades
     * @return lista de DTOs
     */
    public List<UsuarioDto> toDtoList(List<Usuario> usuarios) {
        if (usuarios == null) {
            return null;
        }
        
        return usuarios.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}