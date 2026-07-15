package com.mas.co.service.impl;

import com.mas.co.entity.Cuota;
import com.mas.co.entity.Factura;
import com.mas.co.exception.BusinessException;
import com.mas.co.repository.FacturaRepository;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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

    private final FacturaRepository facturaRepository;


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
        params.put("cargo_fijo", toDecimal(factura.getCargoFijo()));
        params.put("valor_consumo", toDecimal(factura.getValorConsumo()));
        params.put("deuda_anterior", toDecimal(factura.getDeudaAnterior()));
        params.put("otros_cobros", toDecimal(factura.getOtrosCobros()));
        params.put("descripcion", factura.getOtrosCobrosDescripcion());
        params.put("no_pago", toDecimal(factura.getNoPago()));
        params.put("total", df.format(factura.getValorTotal()));
        params.put("consumo_anterior", obtenerConsumoAnterior(factura));
        params.put("mes", factura.getMes());
        params.put("ruta_imagen", rutaImagen);

        // Cuota
        if (factura.getCuota() != null) {
            Cuota cuota = factura.getCuota();
            params.put("descripcion_cuota", cuota.getDescripcion());
            params.put("cuota_actual", cuota.getCuotaActual());
            params.put("numero_de_cuotas", cuota.getNumeroCuota());
            params.put("valor_cuota", toDecimal(cuota.getValorCuota()));
        } else {
            params.put("descripcion_cuota", null);
            params.put("cuota_actual", null);
            params.put("numero_de_cuotas", null);
            params.put("valor_cuota", toDecimal(0.0));
        }

        return params;
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
