package com.mas.co.mapper;

import com.mas.co.dto.CuotaDto;
import com.mas.co.entity.Cuota;
import com.mas.co.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper para conversión entre Cuota y CuotaDto.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@Component
public class CuotaMapper {

    /**
     * Convierte entidad a DTO.
     */
    public CuotaDto toDto(Cuota cuota) {
        if (cuota == null) {
            return null;
        }

        return CuotaDto.builder()
                .id(cuota.getId())
                .descripcion(cuota.getDescripcion())
                .valorCuota(cuota.getValorCuota())
                .valorTotal(cuota.getValorTotal())
                .fechaInsert(cuota.getFechaInsert())
                .numeroCuota(cuota.getNumeroCuota())
                .cuotaActual(cuota.getCuotaActual())
                .activo(cuota.getActivo())
                .usuarioId(cuota.getUsuario().getId())
                .usuarioNombre(cuota.getUsuario().getNombreCompleto())
                .build();
    }

    /**
     * Convierte DTO a entidad.
     */
    public Cuota toEntity(CuotaDto dto) {
        if (dto == null) {
            return null;
        }

        Usuario usuario = Usuario.builder()
                .id(dto.getUsuarioId())
                .build();

        return Cuota.builder()
                .id(dto.getId())
                .descripcion(dto.getDescripcion())
                // valorCuota se calcula automáticamente, no se mapea desde DTO
                .valorTotal(dto.getValorTotal())
                .fechaInsert(dto.getFechaInsert())
                .numeroCuota(dto.getNumeroCuota())
                .cuotaActual(dto.getCuotaActual())
                .activo(dto.getActivo())
                .usuario(usuario)
                .build();
    }

    /**
     * Actualiza entidad con datos del DTO.
     */
    public void updateEntity(CuotaDto dto, Cuota cuota) {
        if (dto == null || cuota == null) {
            return;
        }

        cuota.setDescripcion(dto.getDescripcion());
        // valorCuota se calcula automáticamente, no se actualiza desde DTO
        cuota.setValorTotal(dto.getValorTotal());
        cuota.setNumeroCuota(dto.getNumeroCuota());
        cuota.setCuotaActual(dto.getCuotaActual());
        cuota.setActivo(dto.getActivo());

        if (dto.getUsuarioId() != null && !dto.getUsuarioId().equals(cuota.getUsuario().getId())) {
            Usuario usuario = Usuario.builder()
                    .id(dto.getUsuarioId())
                    .build();
            cuota.setUsuario(usuario);
        }
    }

    /**
     * Convierte lista de entidades a DTOs.
     */
    public List<CuotaDto> toDtoList(List<Cuota> cuotas) {
        return cuotas == null ? null : cuotas.stream().map(this::toDto).toList();
    }
}