package com.mas.co.service.impl;

import com.mas.co.entity.Factura;
import com.mas.co.entity.FacturaCuota;
import com.mas.co.exception.BusinessException;
import com.mas.co.repository.FacturaRepository;
import com.mas.co.service.NotaInformativaService;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacturaPdfService {

    private static final String JRXML_PATH = "/factura/pruebaAcueducto.jrxml";
    private static final String LOGO_PATH = "/images/logo.png";
    private static final int MAX_CONCEPTOS = 8;

    private final FacturaRepository facturaRepository;
    private final NotaInformativaService notaInformativaService;


    public byte[] generarPdf(Long facturaId) {
        log.debug("Generando PDF para factura ID: {}", facturaId);

        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> BusinessException.invoiceNotFound(facturaId));

        try {
            InputStream jrxmlStream = getClass().getResourceAsStream(JRXML_PATH);
            JasperReport report = JasperCompileManager.compileReport(jrxmlStream);

            Map<String, Object> parameters = buildParameters(factura);
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

            byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrint);
            log.info("PDF generado exitosamente para factura ID: {}", facturaId);
            return pdf;
        } catch (Exception e) {
            log.error("Error generando PDF para factura ID: {}", facturaId, e);
            throw new RuntimeException("Error al generar la factura PDF: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> buildParameters(Factura factura) {
        String rutaImagen = getClass().getResource(LOGO_PATH).toString();
        DecimalFormat df = new DecimalFormat("#,##0.00");

        Map<String, Object> params = new HashMap<>();
        params.put("id", factura.getUsuario().getId());
        params.put("nombres", factura.getUsuario().getNombre());
        params.put("apellidos", factura.getUsuario().getApellidos());
        params.put("lectura_actual", factura.getLecturaActual());
        params.put("lectura_anterior", factura.getLecturaAnterior());
        params.put("metros_consumidos", factura.getConsumo());
        params.put("total", df.format(factura.getValorTotal()));
        params.put("consumo_anterior", obtenerConsumoAnterior(factura));
        params.put("mes", factura.getMes());
        params.put("ruta_imagen", rutaImagen);
        params.put("nota", notaInformativaService.obtenerTextoVigente());

        // El reporte tiene un número fijo de filas: las que no se usan van vacías
        List<Concepto> conceptos = buildConceptos(factura);
        List<String> descripciones = new ArrayList<>();
        List<String> valores = new ArrayList<>();
        for (int i = 0; i < MAX_CONCEPTOS; i++) {
            boolean hayConcepto = i < conceptos.size();
            descripciones.add(hayConcepto ? conceptos.get(i).descripcion() : "");
            valores.add(hayConcepto ? "$ " + toDecimal(conceptos.get(i).valor()) : "");
        }
        params.put("conceptos", descripciones);
        params.put("valores", valores);

        return params;
    }

    /**
     * Arma las filas de cobro de la factura. Cada cuota va en su propia fila para que el usuario
     * sepa a qué corresponde cada valor.
     */
    List<Concepto> buildConceptos(Factura factura) {
        List<Concepto> despuesDeCuotas = new ArrayList<>();
        if (esPositivo(factura.getOtrosCobros())) {
            String descripcion = factura.getOtrosCobrosDescripcion();
            despuesDeCuotas.add(new Concepto(
                    descripcion == null || descripcion.isBlank() ? "OTROS COBROS" : descripcion,
                    factura.getOtrosCobros()));
        }
        if (esPositivo(factura.getNoPago())) {
            despuesDeCuotas.add(new Concepto("No Pago", factura.getNoPago()));
        }

        List<Concepto> conceptos = new ArrayList<>();
        conceptos.add(new Concepto("CARGO FIJO", factura.getCargoFijo()));
        conceptos.add(new Concepto("CONSUMO", factura.getValorConsumo()));
        if (esPositivo(factura.getDeudaAnterior())) {
            conceptos.add(new Concepto("DEUDA ANTERIOR", factura.getDeudaAnterior()));
        }

        // Si las cuotas no caben en las filas disponibles, las últimas se agrupan en una sola
        List<FacturaCuota> cuotas = factura.getCuotas();
        int filasParaCuotas = MAX_CONCEPTOS - conceptos.size() - despuesDeCuotas.size();
        int cuotasDetalladas = cuotas.size() <= filasParaCuotas ? cuotas.size() : filasParaCuotas - 1;
        for (FacturaCuota cuota : cuotas.subList(0, cuotasDetalladas)) {
            conceptos.add(new Concepto(describirCuota(cuota), cuota.getValor()));
        }
        List<FacturaCuota> cuotasAgrupadas = cuotas.subList(cuotasDetalladas, cuotas.size());
        if (!cuotasAgrupadas.isEmpty()) {
            conceptos.add(new Concepto(
                    "OTRAS CUOTAS (" + cuotasAgrupadas.size() + ")",
                    cuotasAgrupadas.stream().mapToDouble(FacturaCuota::getValor).sum()));
        }

        conceptos.addAll(despuesDeCuotas);
        return conceptos;
    }

    private String describirCuota(FacturaCuota cuota) {
        // Las facturas anteriores al detalle de cuotas no guardaron el número de cuota cobrado
        Integer numeroCuota = cuota.getNumeroCuota() != null
                ? cuota.getNumeroCuota() : cuota.getCuota().getCuotaActual();
        return String.format("%s (cuota %d de %d)", cuota.getDescripcion(), numeroCuota, cuota.getTotalCuotas());
    }

    private boolean esPositivo(Double value) {
        return value != null && value > 0;
    }

    record Concepto(String descripcion, Double valor) {
    }

    private Integer obtenerConsumoAnterior(Factura factura) {
        String mesAnterior = getMesAnterior(factura.getMes());
        Integer anioAnterior = "Diciembre".equals(mesAnterior)
                ? factura.getAnio() - 1 : factura.getAnio();

        List<Factura> anteriores = facturaRepository.findUltimaLecturaPorUsuario(factura.getUsuario().getId());
        return anteriores.stream()
                .filter(f -> f.getMes().equals(mesAnterior) && f.getAnio().equals(anioAnterior))
                .findFirst()
                .map(Factura::getConsumo)
                .orElse(0);
    }

    private String getMesAnterior(String mes) {
        return switch (mes) {
            case "Enero" -> "Diciembre";
            case "Febrero" -> "Enero";
            case "Marzo" -> "Febrero";
            case "Abril" -> "Marzo";
            case "Mayo" -> "Abril";
            case "Junio" -> "Mayo";
            case "Julio" -> "Junio";
            case "Agosto" -> "Julio";
            case "Septiembre" -> "Agosto";
            case "Octubre" -> "Septiembre";
            case "Noviembre" -> "Octubre";
            case "Diciembre" -> "Noviembre";
            default -> "";
        };
    }

    private BigDecimal toDecimal(Double value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }
}
