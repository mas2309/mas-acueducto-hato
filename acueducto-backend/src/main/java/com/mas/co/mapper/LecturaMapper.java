package com.mas.co.mapper;

import com.mas.co.dto.LecturaDto;
import com.mas.co.entity.Cuota;
import com.mas.co.entity.Factura;
import com.mas.co.entity.Usuario;
import com.mas.co.service.impl.EstadoFacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversión entre Factura y LecturaDto.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class LecturaMapper {

  private final EstadoFacturaService estadoFacturaService;

  /** Convierte entidad Factura a LecturaDto. */
  public LecturaDto toDto(Factura factura) {
    if (factura == null) {
      return null;
    }

    String estadoPago = estadoFacturaService.calcularEstado(
        Boolean.TRUE.equals(factura.getPago()),
        Boolean.TRUE.equals(factura.getPagoBanco()),
        factura.getMes(),
        factura.getAnio()
    );

    return LecturaDto.builder()
        .id(factura.getId())
        .usuarioId(factura.getUsuario().getId())
        .usuarioNombre(factura.getUsuario().getNombreCompleto())
        .mes(factura.getMes())
        .anio(factura.getAnio())
        .lecturaActual(factura.getLecturaActual())
        .lecturaAnterior(factura.getLecturaAnterior())
        .consumo(factura.getConsumo())
        .valorConsumo(factura.getValorConsumo())
        .otrosCobros(factura.getOtrosCobros())
        .otrosCobrosDescripcion(factura.getOtrosCobrosDescripcion())
        .deudaAnterior(factura.getDeudaAnterior())
        .cuotaId(factura.getCuota() != null ? factura.getCuota().getId() : null)
        .valorCuota(factura.getValorCuota())
        .cargoFijo(factura.getCargoFijo())
        .valorTotal(factura.getValorTotal())
        .fechaIngreso(factura.getFechaIngreso())
        .estadoPago(estadoPago)
        .pago(factura.getPago())
        .pagoBanco(factura.getPagoBanco())
        .noPago(factura.getNoPago())
        .build();
  }

  /** Convierte LecturaDto a entidad Factura. */
  public Factura toEntity(LecturaDto dto) {
    if (dto == null) {
      return null;
    }

    Usuario usuario = Usuario.builder().id(dto.getUsuarioId()).build();

    Cuota cuota = null;
    if (dto.getCuotaId() != null) {
      cuota = Cuota.builder().id(dto.getCuotaId()).build();
    }

    return Factura.builder()
        .mes(dto.getMes())
        .anio(dto.getAnio())
        .lecturaActual(dto.getLecturaActual())
        .lecturaAnterior(dto.getLecturaAnterior())
        .consumo(dto.getConsumo())
        .valorConsumo(dto.getValorConsumo())
        .otrosCobros(dto.getOtrosCobros())
        .otrosCobrosDescripcion(dto.getOtrosCobrosDescripcion())
        .deudaAnterior(dto.getDeudaAnterior())
        .valorCuota(dto.getValorCuota())
        .cargoFijo(dto.getCargoFijo())
        .valorTotal(dto.getValorTotal())
        .fechaIngreso(dto.getFechaIngreso())
        .usuario(usuario)
        .cuota(cuota)
        .noPago(dto.getNoPago())
        .build();
  }
}
