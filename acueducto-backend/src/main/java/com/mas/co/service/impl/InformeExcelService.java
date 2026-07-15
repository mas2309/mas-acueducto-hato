package com.mas.co.service.impl;

import com.mas.co.dto.InformeDto;
import com.mas.co.dto.InformeDto.InformeMensualDto;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InformeExcelService {

    public byte[] generarExcel(InformeDto informe, Integer anio, String mes) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CellStyle headerStyle = crearEstiloHeader(wb);
            CellStyle moneyStyle = crearEstiloMoneda(wb);
            CellStyle titleStyle = crearEstiloTitulo(wb);

            // Hoja 1: Resumen
            Sheet resumen = wb.createSheet("Resumen");
            escribirResumen(resumen, informe, anio, mes, titleStyle, headerStyle, moneyStyle);

            // Hoja 2: Detalle Mensual
            if (informe.getDetalleMensual() != null && !informe.getDetalleMensual().isEmpty()) {
                Sheet detalle = wb.createSheet("Detalle Mensual");
                escribirDetalleMensual(detalle, informe, headerStyle, moneyStyle);
            }

            // Hoja 3: Método de Pago
            Sheet pagos = wb.createSheet("Método de Pago");
            escribirMetodoPago(pagos, informe, headerStyle, moneyStyle, titleStyle);

            wb.write(out);
            return out.toByteArray();
        }
    }

    private void escribirResumen(Sheet sheet, InformeDto inf, Integer anio, String mes,
                                  CellStyle titleStyle, CellStyle headerStyle, CellStyle moneyStyle) {
        int row = 0;

        // Título
        Row titleRow = sheet.createRow(row++);
        titleRow.createCell(0).setCellValue("INFORME DE INGRESOS - ACUEDUCTO EL HATO");
        titleRow.getCell(0).setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        Row periodoRow = sheet.createRow(row++);
        periodoRow.createCell(0).setCellValue("Período: " + (mes != null && !mes.isEmpty() ? mes + " " : "") + anio);
        row++;

        // Resumen general
        Row h = sheet.createRow(row++);
        h.createCell(0).setCellValue("CONCEPTO");
        h.createCell(1).setCellValue("VALOR ($)");
        h.createCell(2).setCellValue("PORCENTAJE");
        h.getCell(0).setCellStyle(headerStyle);
        h.getCell(1).setCellStyle(headerStyle);
        h.getCell(2).setCellStyle(headerStyle);

        double total = inf.getTotalIngresos();
        agregarFilaResumen(sheet, row++, "Consumo", inf.getTotalConsumo(), total, moneyStyle);
        agregarFilaResumen(sheet, row++, "Cargo Fijo", inf.getTotalCargoFijo(), total, moneyStyle);
        agregarFilaResumen(sheet, row++, "Cuotas", inf.getTotalCuotas(), total, moneyStyle);
        agregarFilaResumen(sheet, row++, "Otros Cobros", inf.getTotalOtrosCobros(), total, moneyStyle);
        agregarFilaResumen(sheet, row++, "No Pago", inf.getTotalNoPago(), total, moneyStyle);
        agregarFilaResumen(sheet, row++, "Deuda Anterior", inf.getTotalDeudaAnterior(), total, moneyStyle);
        row++;

        Row totalRow = sheet.createRow(row++);
        totalRow.createCell(0).setCellValue("TOTAL INGRESOS");
        totalRow.createCell(1).setCellValue(inf.getTotalIngresos());
        totalRow.getCell(0).setCellStyle(headerStyle);
        totalRow.getCell(1).setCellStyle(moneyStyle);
        row++;

        // Estadísticas
        Row statsH = sheet.createRow(row++);
        statsH.createCell(0).setCellValue("ESTADÍSTICAS");
        statsH.getCell(0).setCellStyle(headerStyle);

        sheet.createRow(row++).createCell(0); // espacio
        crearFilaSimple(sheet, row++, "Total Facturas", String.valueOf(inf.getTotalFacturas()));
        crearFilaSimple(sheet, row++, "Facturas Pagadas", String.valueOf(inf.getFacturasPagadas()));
        crearFilaSimple(sheet, row++, "Facturas Pendientes", String.valueOf(inf.getFacturasPendientes()));
        crearFilaSimple(sheet, row++, "Facturas Vencidas", String.valueOf(inf.getFacturasVencidas()));
        crearFilaSimple(sheet, row++, "Consumo Total (m³)", String.valueOf(inf.getTotalMetrosCubicos()));

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
    }

    private void escribirDetalleMensual(Sheet sheet, InformeDto inf, CellStyle headerStyle, CellStyle moneyStyle) {
        int row = 0;
        Row h = sheet.createRow(row++);
        String[] headers = {"Mes", "Facturas", "Consumo ($)", "Cargo Fijo ($)", "Cuotas ($)",
                "Otros ($)", "No Pago ($)", "Total ($)", "Efectivo ($)", "Banco ($)", "m³", "Pagadas", "Pendientes"};
        for (int i = 0; i < headers.length; i++) {
            h.createCell(i).setCellValue(headers[i]);
            h.getCell(i).setCellStyle(headerStyle);
        }

        for (InformeMensualDto m : inf.getDetalleMensual()) {
            Row r = sheet.createRow(row++);
            r.createCell(0).setCellValue(m.getMes());
            r.createCell(1).setCellValue(m.getCantidadFacturas());
            r.createCell(2).setCellValue(m.getIngresoConsumo());
            r.getCell(2).setCellStyle(moneyStyle);
            r.createCell(3).setCellValue(m.getIngresoCargoFijo());
            r.getCell(3).setCellStyle(moneyStyle);
            r.createCell(4).setCellValue(m.getIngresoCuotas());
            r.getCell(4).setCellStyle(moneyStyle);
            r.createCell(5).setCellValue(m.getIngresoOtrosCobros());
            r.getCell(5).setCellStyle(moneyStyle);
            r.createCell(6).setCellValue(m.getIngresoNoPago());
            r.getCell(6).setCellStyle(moneyStyle);
            r.createCell(7).setCellValue(m.getIngresoTotal());
            r.getCell(7).setCellStyle(moneyStyle);
            r.createCell(8).setCellValue(m.getIngresoEfectivo());
            r.getCell(8).setCellStyle(moneyStyle);
            r.createCell(9).setCellValue(m.getIngresoBanco());
            r.getCell(9).setCellStyle(moneyStyle);
            r.createCell(10).setCellValue(m.getConsumoM3());
            r.createCell(11).setCellValue(m.getPagadas());
            r.createCell(12).setCellValue(m.getPendientes());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void escribirMetodoPago(Sheet sheet, InformeDto inf, CellStyle headerStyle,
                                     CellStyle moneyStyle, CellStyle titleStyle) {
        int row = 0;
        Row titleRow = sheet.createRow(row++);
        titleRow.createCell(0).setCellValue("DISCRIMINACIÓN POR MÉTODO DE PAGO");
        titleRow.getCell(0).setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        row++;

        Row h = sheet.createRow(row++);
        h.createCell(0).setCellValue("MÉTODO");
        h.createCell(1).setCellValue("VALOR ($)");
        h.createCell(2).setCellValue("FACTURAS");
        h.getCell(0).setCellStyle(headerStyle);
        h.getCell(1).setCellStyle(headerStyle);
        h.getCell(2).setCellStyle(headerStyle);

        Row r1 = sheet.createRow(row++);
        r1.createCell(0).setCellValue("Efectivo");
        r1.createCell(1).setCellValue(inf.getIngresoEfectivo());
        r1.getCell(1).setCellStyle(moneyStyle);
        r1.createCell(2).setCellValue(inf.getFacturasPagadasEfectivo());

        Row r2 = sheet.createRow(row++);
        r2.createCell(0).setCellValue("Banco");
        r2.createCell(1).setCellValue(inf.getIngresoBanco());
        r2.getCell(1).setCellStyle(moneyStyle);
        r2.createCell(2).setCellValue(inf.getFacturasPagadasBanco());

        Row r3 = sheet.createRow(row++);
        r3.createCell(0).setCellValue("Pendiente/Vencida");
        r3.createCell(1).setCellValue(inf.getIngresoPendiente());
        r3.getCell(1).setCellStyle(moneyStyle);
        r3.createCell(2).setCellValue(inf.getFacturasPendientes() + inf.getFacturasVencidas());

        row++;
        Row totalRow = sheet.createRow(row);
        totalRow.createCell(0).setCellValue("TOTAL");
        totalRow.createCell(1).setCellValue(inf.getTotalIngresos());
        totalRow.getCell(0).setCellStyle(headerStyle);
        totalRow.getCell(1).setCellStyle(moneyStyle);
        totalRow.createCell(2).setCellValue(inf.getTotalFacturas());

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
    }

    private void agregarFilaResumen(Sheet sheet, int rowNum, String concepto, double valor, double total, CellStyle moneyStyle) {
        Row r = sheet.createRow(rowNum);
        r.createCell(0).setCellValue(concepto);
        r.createCell(1).setCellValue(valor);
        r.getCell(1).setCellStyle(moneyStyle);
        r.createCell(2).setCellValue(total > 0 ? String.format("%.1f%%", valor * 100 / total) : "0%");
    }

    private void crearFilaSimple(Sheet sheet, int rowNum, String label, String value) {
        Row r = sheet.createRow(rowNum);
        r.createCell(0).setCellValue(label);
        r.createCell(1).setCellValue(value);
    }

    private CellStyle crearEstiloHeader(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloMoneda(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle crearEstiloTitulo(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }
}
