package com.mas.co.service.impl;

import com.mas.co.dto.InformeDto;
import com.mas.co.dto.InformeDto.InformeMensualDto;
import com.mas.co.entity.Factura;
import com.mas.co.repository.FacturaRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InformeService {

    private static final List<String> MESES_ORDEN = List.of(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    );

    private final FacturaRepository facturaRepository;
    private final EstadoFacturaService estadoFacturaService;

    public List<Integer> obtenerAniosDisponibles() {
        return facturaRepository.findAniosDisponibles();
    }

    public InformeDto generarInformeAnual(Integer anio) {
        log.debug("Generando informe anual para: {}", anio);
        List<Factura> facturas = facturaRepository.findByAnio(anio);
        return construirInforme(facturas);
    }

    public InformeDto generarInformeMensual(String mes, Integer anio) {
        log.debug("Generando informe mensual para: {} {}", mes, anio);
        List<Factura> facturas = facturaRepository.findByMesAndAnio(mes, anio);
        return construirInforme(facturas);
    }

    private InformeDto construirInforme(List<Factura> facturas) {
        InformeDto.InformeDtoBuilder builder = InformeDto.builder();
        calcularTotales(facturas, builder);
        calcularDatosMensuales(facturas, builder);
        return builder.build();
    }

    private void calcularTotales(List<Factura> facturas, InformeDto.InformeDtoBuilder builder) {
        double totalConsumo = 0, totalCargoFijo = 0, totalCuotas = 0;
        double totalOtros = 0, totalNoPago = 0, totalDeuda = 0, totalIngresos = 0;
        double ingEfectivo = 0, ingBanco = 0, ingPendiente = 0;
        int totalM3 = 0, pagadas = 0, pagEfectivo = 0, pagBanco = 0, pendientes = 0, vencidas = 0;

        for (Factura f : facturas) {
            totalConsumo += val(f.getValorConsumo());
            totalCargoFijo += val(f.getCargoFijo());
            totalCuotas += val(f.getValorCuota());
            totalOtros += val(f.getOtrosCobros());
            totalNoPago += val(f.getNoPago());
            totalDeuda += val(f.getDeudaAnterior());
            totalIngresos += val(f.getValorTotal());
            totalM3 += f.getConsumo() != null ? f.getConsumo() : 0;

            String estado = estadoFacturaService.calcularEstado(
                    Boolean.TRUE.equals(f.getPago()),
                    Boolean.TRUE.equals(f.getPagoBanco()),
                    f.getMes(), f.getAnio());

            switch (estado) {
                case "EFECTIVO" -> {
                    pagadas++;
                    pagEfectivo++;
                    ingEfectivo += val(f.getValorTotal());
                }
                case "BANCO" -> {
                    pagadas++;
                    pagBanco++;
                    ingBanco += val(f.getValorTotal());
                }
                case "VENCIDA" -> {
                    vencidas++;
                    ingPendiente += val(f.getValorTotal());
                }
                default -> {
                    pendientes++;
                    ingPendiente += val(f.getValorTotal());
                }
            }
        }

        builder.totalIngresos(totalIngresos).totalConsumo(totalConsumo)
                .totalCargoFijo(totalCargoFijo).totalCuotas(totalCuotas)
                .totalOtrosCobros(totalOtros).totalNoPago(totalNoPago)
                .totalDeudaAnterior(totalDeuda)
                .ingresoEfectivo(ingEfectivo).ingresoBanco(ingBanco)
                .ingresoPendiente(ingPendiente)
                .totalMetrosCubicos(totalM3).totalFacturas(facturas.size())
                .facturasPagadas(pagadas).facturasPagadasEfectivo(pagEfectivo)
                .facturasPagadasBanco(pagBanco)
                .facturasPendientes(pendientes).facturasVencidas(vencidas);
    }

    private void calcularDatosMensuales(List<Factura> facturas,
                                         InformeDto.InformeDtoBuilder builder) {
        Map<String, List<Factura>> porMes = facturas.stream()
                .collect(Collectors.groupingBy(Factura::getMes));

        List<String> meses = new ArrayList<>();
        List<Double> ingresosMensuales = new ArrayList<>();
        List<Double> ingEfectivoMensual = new ArrayList<>();
        List<Double> ingBancoMensual = new ArrayList<>();
        List<Integer> consumoMensualM3 = new ArrayList<>();
        List<Integer> facturasGeneradas = new ArrayList<>();
        List<InformeMensualDto> detalleMensual = new ArrayList<>();

        for (String mes : MESES_ORDEN) {
            List<Factura> delMes = porMes.getOrDefault(mes, List.of());
            if (delMes.isEmpty()) {
                continue;
            }

            InformeMensualDto detalle = construirDetalleMes(delMes, mes);
            meses.add(mes);
            ingresosMensuales.add(detalle.getIngresoTotal());
            ingEfectivoMensual.add(detalle.getIngresoEfectivo());
            ingBancoMensual.add(detalle.getIngresoBanco());
            consumoMensualM3.add(detalle.getConsumoM3());
            facturasGeneradas.add(detalle.getCantidadFacturas());
            detalleMensual.add(detalle);
        }

        builder.meses(meses).ingresosMensuales(ingresosMensuales)
                .ingresosEfectivoMensual(ingEfectivoMensual)
                .ingresosBancoMensual(ingBancoMensual)
                .consumoMensualM3(consumoMensualM3)
                .facturasGeneradasMensual(facturasGeneradas)
                .detalleMensual(detalleMensual);
    }

    private InformeMensualDto construirDetalleMes(List<Factura> delMes, String mes) {
        double ingConsumo = 0, ingCargo = 0, ingCuotas = 0;
        double ingOtros = 0, ingNoPago = 0, ingTotal = 0;
        double efecMes = 0, bancoMes = 0;
        int m3 = 0, pag = 0, pagE = 0, pagB = 0, pend = 0;

        for (Factura f : delMes) {
            ingConsumo += val(f.getValorConsumo());
            ingCargo += val(f.getCargoFijo());
            ingCuotas += val(f.getValorCuota());
            ingOtros += val(f.getOtrosCobros());
            ingNoPago += val(f.getNoPago());
            ingTotal += val(f.getValorTotal());
            m3 += f.getConsumo() != null ? f.getConsumo() : 0;

            if (Boolean.TRUE.equals(f.getPago())) {
                pag++;
                pagE++;
                efecMes += val(f.getValorTotal());
            } else if (Boolean.TRUE.equals(f.getPagoBanco())) {
                pag++;
                pagB++;
                bancoMes += val(f.getValorTotal());
            } else {
                pend++;
            }
        }

        return InformeMensualDto.builder()
                .mes(mes).cantidadFacturas(delMes.size())
                .ingresoConsumo(ingConsumo).ingresoCargoFijo(ingCargo)
                .ingresoCuotas(ingCuotas).ingresoOtrosCobros(ingOtros)
                .ingresoNoPago(ingNoPago).ingresoTotal(ingTotal)
                .ingresoEfectivo(efecMes).ingresoBanco(bancoMes)
                .consumoM3(m3).pagadas(pag)
                .pagadasEfectivo(pagE).pagadasBanco(pagB)
                .pendientes(pend).build();
    }

    private double val(Double v) {
        return v != null ? v : 0.0;
    }
}
