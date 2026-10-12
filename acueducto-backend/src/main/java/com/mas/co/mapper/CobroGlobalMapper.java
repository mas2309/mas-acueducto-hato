package com.mas.co.mapper;

import com.mas.co.dto.CobroGlobalDto;
import com.mas.co.entity.CobroGlobal;
import com.mas.co.entity.Cuota;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper para conversión entre CobroGlobal y CobroGlobalDto.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Component
public class CobroGlobalMapper {

    /**
     * Convierte entidad a DTO, calculando el resumen de avance a partir de sus cuotas.
     */
    public CobroGlobalDto toDto(CobroGlobal cobroGlobal, List<Cuota> cuotas) {
        if (cobroGlobal == null) {
            return null;
        }

        CobroGlobalDto dto = CobroGlobalDto.builder()
                .id(cobroGlobal.getId())
                .descripcion(cobroGlobal.getDescripcion())
                .valorTotal(cobroGlobal.getValorTotal())
                .numeroCuotas(cobroGlobal.getNumeroCuotas())
                .valorCuota(cobroGlobal.getValorTotal() / cobroGlobal.getNumeroCuotas())
                .fechaCreacion(cobroGlobal.getFechaCreacion())
                .activo(cobroGlobal.getActivo())
                .totalUsuarios(cuotas.size())
                .build();

        for (Cuota cuota : cuotas) {
            int cuotasFacturadas = Math.min(cuota.getCuotaActual(), cuota.getNumeroCuota());
            dto.setValorEsperado(dto.getValorEsperado() + cuota.getValorTotal());
            dto.setValorFacturado(dto.getValorFacturado() + cuotasFacturadas * cuota.getValorCuota());

            if (Boolean.TRUE.equals(cuota.getPagoAnticipado())) {
                dto.setUsuariosPagoAnticipado(dto.getUsuariosPagoAnticipado() + 1);
                dto.setValorAnticipado(dto.getValorAnticipado() + cuota.getValorRestante());
            } else if (cuota.isActiva()) {
                dto.setUsuariosEnCobro(dto.getUsuariosEnCobro() + 1);
                dto.setValorPendiente(dto.getValorPendiente() + cuota.getValorRestante());
            } else if (cuota.getCuotaActual() >= cuota.getNumeroCuota()) {
                dto.setUsuariosCompletados(dto.getUsuariosCompletados() + 1);
            } else {
                dto.setUsuariosDesactivados(dto.getUsuariosDesactivados() + 1);
            }
        }

        return dto;
    }

    /**
     * Convierte DTO a entidad.
     */
    public CobroGlobal toEntity(CobroGlobalDto dto) {
        if (dto == null) {
            return null;
        }

        return CobroGlobal.builder()
                .descripcion(dto.getDescripcion())
                .valorTotal(dto.getValorTotal())
                .numeroCuotas(dto.getNumeroCuotas())
                .build();
    }
}
