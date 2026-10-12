package com.mas.co.service.impl;

import com.mas.co.constants.BusinessConstants;
import com.mas.co.dto.InformeDto;
import com.mas.co.dto.InformeDto.DeudorDto;
import com.mas.co.dto.InformeDto.InformeMensualDto;
import com.mas.co.dto.InformeDto.RangoConsumoDto;
import com.mas.co.entity.Factura;
import com.mas.co.entity.Valores;
import com.mas.co.repository.FacturaRepository;
import com.mas.co.service.ValorService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    private final ValorService valorService;

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
        builder.rangosConsumo(calcularRangosConsumo(facturas));
        List<Factura> ultimasImpagas = facturaRepository.findUltimasFacturasPorUsuario().stream()
                .filter(f -> !f.isPagada())
                .toList();
        builder.totalDeudaPendienteReal(calcularDeudaPendienteReal(ultimasImpagas));
        builder.deudores(calcularDeudores(ultimasImpagas));
        return builder.build();
    }

    /**
     * Calcula la deuda pendiente real a la fecha, a nivel de todo el sistema (no solo del
     * período filtrado): toma la última factura de cada usuario y, si sigue impaga, suma su
     * valorTotal. Evita el doble conteo de {@code totalDeudaAnterior}, que al ser un componente
     * de cada factura individual se arrastra y se re-factura mes a mes mientras la deuda persiste.
     */
    private double calcularDeudaPendienteReal(List<Factura> ultimasImpagas) {
        return ultimasImpagas.stream()
                .mapToDouble(f -> val(f.getValorTotal()))
                .sum();
    }

    /**
     * Lista, del que más debe al que menos, los usuarios con facturas vencidas a la fecha (no
     * solo del período filtrado). La deuda es el valorTotal de su última factura, que ya incluye
     * lo arrastrado. Quien solo tiene pendiente una factura que aún está en plazo no aparece.
     */
    private List<DeudorDto> calcularDeudores(List<Factura> ultimasImpagas) {
        if (ultimasImpagas.isEmpty()) {
            return List.of();
        }

        Set<Long> usuarioIds = ultimasImpagas.stream()
                .map(f -> f.getUsuario().getId())
                .collect(Collectors.toSet());

        return facturaRepository.findByUsuarioIdIn(usuarioIds).stream()
                .collect(Collectors.groupingBy(f -> f.getUsuario().getId()))
                .values().stream()
                .map(this::construirDeudor)
                .flatMap(Optional::stream)
                .sorted(Comparator.comparingDouble(DeudorDto::getDeuda)
                        .thenComparingInt(DeudorDto::getFacturasSinPagar).reversed())
                .toList();
    }

    private Optional<DeudorDto> construirDeudor(List<Factura> facturasUsuario) {
        List<Factura> recientesPrimero = facturasUsuario.stream()
                .sorted(Comparator.comparingInt(this::ordenPeriodo).reversed())
                .toList();

        // Pagar una factura salda las anteriores, que quedan marcadas como impagas: solo cuentan
        // las seguidas sin pagar desde la más reciente.
        List<Factura> sinPagar = recientesPrimero.stream().takeWhile(f -> !f.isPagada()).toList();
        if (sinPagar.isEmpty()) {
            return Optional.empty();
        }
        Factura masAntigua = sinPagar.get(sinPagar.size() - 1);
        if (!estadoFacturaService.estaVencida(masAntigua.getMes(), masAntigua.getAnio())) {
            return Optional.empty();
        }

        Factura ultima = sinPagar.get(0);
        String ultimoPago = recientesPrimero.stream()
                .skip(sinPagar.size())
                .findFirst()
                .map(f -> f.getFechaPago() != null ? f.getFechaPago().toString() : periodo(f))
                .orElse(null);

        return Optional.of(DeudorDto.builder()
                .usuarioId(ultima.getUsuario().getId())
                .usuarioNombre(ultima.getUsuario().getNombreCompleto())
                .usuarioActivo(ultima.getUsuario().isActivo())
                .deuda(val(ultima.getValorTotal()))
                .facturasSinPagar(sinPagar.size())
                .debeDesde(periodo(masAntigua))
                .ultimoPago(ultimoPago)
                .build());
    }

    /**
     * Agrupa las facturas del período según el rango de tarifa en que cae su consumo. Los límites
     * son los de las tarifas vigentes, también para facturas generadas con tarifas anteriores.
     */
    private List<RangoConsumoDto> calcularRangosConsumo(List<Factura> facturas) {
        Valores valores = valorService.obtenerValores();
        int limiteUno = valores.getRangoUno().intValue();
        int limiteDos = valores.getRangoDos().intValue();
        int limiteTres = valores.getRangoTres().intValue();

        List<RangoConsumoDto> rangos = List.of(
                rango("Sin consumo", "0 m³", null),
                rango("Rango 1", "1 a " + limiteUno + " m³", valores.getValorUno()),
                rango("Rango 2", (limiteUno + 1) + " a " + limiteDos + " m³", valores.getValorDos()),
                rango("Rango 3", (limiteDos + 1) + " a " + limiteTres + " m³", valores.getValorTres()),
                rango("Rango 4", "Más de " + limiteTres + " m³", valores.getValorCuatro()));
        List<Set<Long>> usuariosPorRango = rangos.stream().map(r -> (Set<Long>) new HashSet<Long>()).toList();

        double valorTotal = 0;
        for (Factura f : facturas) {
            int consumo = f.getConsumo() != null ? f.getConsumo() : 0;
            int indice;
            if (consumo <= 0) {
                indice = 0;
            } else if (consumo <= limiteUno) {
                indice = 1;
            } else if (consumo <= limiteDos) {
                indice = 2;
            } else if (consumo <= limiteTres) {
                indice = 3;
            } else {
                indice = 4;
            }

            RangoConsumoDto rango = rangos.get(indice);
            rango.setFacturas(rango.getFacturas() + 1);
            rango.setConsumoM3(rango.getConsumoM3() + consumo);
            rango.setValorConsumo(rango.getValorConsumo() + val(f.getValorConsumo()));
            usuariosPorRango.get(indice).add(f.getUsuario().getId());
            valorTotal += val(f.getValorConsumo());
        }

        for (int i = 0; i < rangos.size(); i++) {
            RangoConsumoDto rango = rangos.get(i);
            rango.setUsuarios(usuariosPorRango.get(i).size());
            rango.setPorcentajeFacturas(porcentaje(rango.getFacturas(), facturas.size()));
            rango.setPorcentajeValor(porcentaje(rango.getValorConsumo(), valorTotal));
        }

        // "Sin consumo" solo se muestra si hay facturas en cero
        return rangos.stream().filter(r -> r.getValorMetroCubico() != null || r.getFacturas() > 0).toList();
    }

    private RangoConsumoDto rango(String nombre, String descripcion, Integer valorMetroCubico) {
        return RangoConsumoDto.builder()
                .nombre(nombre).descripcion(descripcion).valorMetroCubico(valorMetroCubico)
                .build();
    }

    private int porcentaje(double parte, double total) {
        return total > 0 ? (int) Math.round(parte * 100 / total) : 0;
    }

    private int ordenPeriodo(Factura f) {
        return f.getAnio() * 100 + BusinessConstants.MONTH_TO_INT.getOrDefault(f.getMes(), 0);
    }

    private String periodo(Factura f) {
        return f.getMes() + " " + f.getAnio();
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
