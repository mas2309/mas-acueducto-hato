package com.mas.co.mapper;

import com.mas.co.dto.GastoDto;
import com.mas.co.entity.Gasto;
import com.mas.co.security.AdminUser;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GastoMapper {

    public GastoDto toDto(Gasto gasto) {
        if (gasto == null) {
            return null;
        }

        return GastoDto.builder()
                .id(gasto.getId())
                .descripcion(gasto.getDescripcion())
                .monto(gasto.getMonto())
                .fecha(gasto.getFecha())
                .categoria(gasto.getCategoria())
                .pagado(gasto.getPagado())
                .fechaPago(gasto.getFechaPago())
                .soporteUrl(gasto.getSoporteUrl())
                .soporteNombre(gasto.getSoporteNombre())
                .responsable(gasto.getResponsable())
                .registradoPorNombre(gasto.getRegistradoPor() != null ? gasto.getRegistradoPor().getNombreCompleto() : null)
                .registradoPorId(gasto.getRegistradoPor() != null ? gasto.getRegistradoPor().getId() : null)
                .fechaRegistro(gasto.getFechaRegistro())
                .build();
    }

    public Gasto toEntity(GastoDto dto, AdminUser registradoPor) {
        if (dto == null) {
            return null;
        }

        return Gasto.builder()
                .id(dto.getId())
                .descripcion(dto.getDescripcion())
                .monto(dto.getMonto())
                .fecha(dto.getFecha())
                .categoria(dto.getCategoria())
                .pagado(dto.getPagado() != null ? dto.getPagado() : false)
                .fechaPago(dto.getFechaPago())
                .responsable(dto.getResponsable())
                .registradoPor(registradoPor)
                .build();
    }

    public List<GastoDto> toDtoList(List<Gasto> gastos) {
        return gastos == null ? null : gastos.stream().map(this::toDto).toList();
    }
}
