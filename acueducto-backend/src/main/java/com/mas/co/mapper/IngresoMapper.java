package com.mas.co.mapper;

import com.mas.co.dto.IngresoDto;
import com.mas.co.entity.Factura;
import com.mas.co.entity.Ingreso;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class IngresoMapper {

    public IngresoDto toDto(Ingreso ingreso) {
        if (ingreso == null) {
            return null;
        }

        return IngresoDto.builder()
                .id(ingreso.getId())
                .descripcion(ingreso.getDescripcion())
                .monto(ingreso.getMonto())
                .fecha(ingreso.getFecha())
                .categoria(ingreso.getCategoria())
                .soporteUrl(ingreso.getSoporteUrl())
                .soporteNombre(ingreso.getSoporteNombre())
                .facturaId(ingreso.getFactura() != null ? ingreso.getFactura().getId() : null)
                .fechaRegistro(ingreso.getFechaRegistro())
                .build();
    }

    public Ingreso toEntity(IngresoDto dto) {
        if (dto == null) {
            return null;
        }

        Ingreso.IngresoBuilder builder = Ingreso.builder()
                .id(dto.getId())
                .descripcion(dto.getDescripcion())
                .monto(dto.getMonto())
                .fecha(dto.getFecha())
                .categoria(dto.getCategoria());

        if (dto.getFacturaId() != null) {
            builder.factura(Factura.builder().id(dto.getFacturaId()).build());
        }

        return builder.build();
    }

    public List<IngresoDto> toDtoList(List<Ingreso> ingresos) {
        return ingresos == null ? null : ingresos.stream().map(this::toDto).toList();
    }
}
