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

    // Deuda pendiente real a la fecha (sin duplicar arrastre entre meses): suma, por usuario,
    // el valorTotal de su última factura si esta sigue impaga.
    private double totalDeudaPendienteReal;

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

    // Usuarios con facturas vencidas a la fecha, del que más debe al que menos
    private List<DeudorDto> deudores;

    // Facturas del período agrupadas por el rango de tarifa en que cae su consumo
    private List<RangoConsumoDto> rangosConsumo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RangoConsumoDto {
        private String nombre;
        // Consumo que abarca el rango, ej. "11 a 20 m³"
        private String descripcion;
        // Valor del m³ en el rango según las tarifas vigentes. Nulo en "Sin consumo"
        private Integer valorMetroCubico;
        private int usuarios;
        private int facturas;
        private int consumoM3;
        private double valorConsumo;
        private int porcentajeFacturas;
        private int porcentajeValor;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeudorDto {
        private Long usuarioId;
        private String usuarioNombre;
        private boolean usuarioActivo;
        // valorTotal de la última factura: ya incluye lo arrastrado de las anteriores
        private double deuda;
        // Facturas seguidas sin pagar, contando desde la más reciente
        private int facturasSinPagar;
        // Período de la factura impaga más antigua, ej. "Marzo 2026"
        private String debeDesde;
        // Fecha del último pago, o su período si no quedó fecha. Nulo si nunca ha pagado
        private String ultimoPago;
    }

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
