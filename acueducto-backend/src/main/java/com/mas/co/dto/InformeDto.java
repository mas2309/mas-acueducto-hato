package com.mas.co.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformeDto {

    // Totales generales
    private double totalIngresos;
    private double totalConsumo;
    private double totalCargoFijo;
    private double totalCuotas;
    private double totalOtrosCobros;
    private double totalNoPago;
    private double totalDeudaAnterior;

    // Discriminación por método de pago
    private double ingresoEfectivo;
    private double ingresoBanco;
    private double ingresoPendiente;

    // Consumo en m³
    private int totalMetrosCubicos;

    // Contadores
    private int totalFacturas;
    private int facturasPagadas;
    private int facturasPagadasEfectivo;
    private int facturasPagadasBanco;
    private int facturasPendientes;
    private int facturasVencidas;

    // Datos mensuales para gráficos
    private List<String> meses;
    private List<Double> ingresosMensuales;
    private List<Double> ingresosEfectivoMensual;
    private List<Double> ingresosBancoMensual;
    private List<Integer> consumoMensualM3;
    private List<Integer> facturasGeneradasMensual;

    // Discriminación mensual detallada
    private List<InformeMensualDto> detalleMensual;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InformeMensualDto {
        private String mes;
        private int cantidadFacturas;
        private double ingresoConsumo;
        private double ingresoCargoFijo;
        private double ingresoCuotas;
        private double ingresoOtrosCobros;
        private double ingresoNoPago;
        private double ingresoTotal;
        private double ingresoEfectivo;
        private double ingresoBanco;
        private int consumoM3;
        private int pagadas;
        private int pagadasEfectivo;
        private int pagadasBanco;
        private int pendientes;
    }
}
