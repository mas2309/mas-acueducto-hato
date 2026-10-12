package com.mas.co.service.impl;

import com.mas.co.dto.LecturaDto;
import com.mas.co.dto.UsuarioDto;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
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
public class ListadoExcelService {

    private static final int COLUMNA_USUARIO = 2;
    private static final int COLUMNA_DESCRIPCION = 12;
    private static final int ANCHO_NORMAL = 18;
    private static final int ANCHO_TEXTO = 32;

    public byte[] generarExcelUsuarios(List<UsuarioDto> usuarios, String filtro) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle headerStyle = crearEstiloHeader(wb);
            CellStyle titleStyle = crearEstiloTitulo(wb);

            Sheet sheet = wb.createSheet("Usuarios");
            String[] headers = {"ID", "Nombres", "Apellidos", "Fecha Registro", "Estado"};
            int row = escribirEncabezado(sheet, "USUARIOS - ACUEDUCTO EL HATO", filtro, headers, titleStyle, headerStyle);

            for (UsuarioDto u : usuarios) {
                Row r = sheet.createRow(row++);
                r.createCell(0).setCellValue(u.getId());
                r.createCell(1).setCellValue(u.getNombre());
                r.createCell(2).setCellValue(u.getApellidos());
                r.createCell(3).setCellValue(u.getFechaInsert() != null ? u.getFechaInsert().toString() : "");
                r.createCell(4).setCellValue(Boolean.TRUE.equals(u.getActivo()) ? "Activo" : "Inactivo");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generarExcelFacturas(List<LecturaDto> facturas, String filtro) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle headerStyle = crearEstiloHeader(wb);
            CellStyle moneyStyle = crearEstiloMoneda(wb);
            CellStyle titleStyle = crearEstiloTitulo(wb);

            Sheet sheet = wb.createSheet("Facturas");
            String[] headers = {"ID", "ID Usuario", "Usuario", "Mes", "Año", "Lectura Anterior", "Lectura Actual",
                    "Consumo (m³)", "Valor Consumo ($)", "Cargo Fijo ($)", "Cuotas ($)", "Otros Cobros ($)",
                    "Descripción Otros Cobros", "Deuda Anterior ($)", "No Pago ($)", "Total ($)", "Estado",
                    "Fecha Ingreso"};
            int row = escribirEncabezado(sheet, "FACTURAS - ACUEDUCTO EL HATO", filtro, headers, titleStyle, headerStyle);

            double total = 0;
            for (LecturaDto f : facturas) {
                Row r = sheet.createRow(row++);
                r.createCell(0).setCellValue(f.getId());
                r.createCell(1).setCellValue(f.getUsuarioId());
                r.createCell(2).setCellValue(f.getUsuarioNombre());
                r.createCell(3).setCellValue(f.getMes());
                r.createCell(4).setCellValue(f.getAnio());
                r.createCell(5).setCellValue(f.getLecturaAnterior());
                r.createCell(6).setCellValue(f.getLecturaActual());
                r.createCell(7).setCellValue(f.getConsumo());
                crearCeldaMoneda(r, 8, f.getValorConsumo(), moneyStyle);
                crearCeldaMoneda(r, 9, f.getCargoFijo(), moneyStyle);
                crearCeldaMoneda(r, 10, f.getValorCuota(), moneyStyle);
                crearCeldaMoneda(r, 11, f.getOtrosCobros(), moneyStyle);
                r.createCell(12).setCellValue(f.getOtrosCobrosDescripcion() != null ? f.getOtrosCobrosDescripcion() : "");
                crearCeldaMoneda(r, 13, f.getDeudaAnterior(), moneyStyle);
                crearCeldaMoneda(r, 14, f.getNoPago(), moneyStyle);
                crearCeldaMoneda(r, 15, f.getValorTotal(), moneyStyle);
                r.createCell(16).setCellValue(describirEstado(f.getEstadoPago()));
                r.createCell(17).setCellValue(f.getFechaIngreso() != null ? f.getFechaIngreso().toString() : "");
                total += val(f.getValorTotal());
            }

            row++;
            Row totalRow = sheet.createRow(row);
            totalRow.createCell(14).setCellValue("TOTAL:");
            totalRow.getCell(14).setCellStyle(headerStyle);
            totalRow.createCell(15).setCellValue(total);
            totalRow.getCell(15).setCellStyle(moneyStyle);

            // Anchos fijos: autoSizeColumn es muy lento con miles de facturas
            for (int i = 0; i < headers.length; i++) {
                boolean esTexto = i == COLUMNA_USUARIO || i == COLUMNA_DESCRIPCION;
                sheet.setColumnWidth(i, (esTexto ? ANCHO_TEXTO : ANCHO_NORMAL) * 256);
            }

            wb.write(out);
            return out.toByteArray();
        }
    }

    /** Escribe título, filtro aplicado y fila de encabezados. Devuelve la fila donde siguen los datos. */
    private int escribirEncabezado(Sheet sheet, String titulo, String filtro, String[] headers,
                                    CellStyle titleStyle, CellStyle headerStyle) {
        int row = 0;
        Row titleRow = sheet.createRow(row++);
        titleRow.createCell(0).setCellValue(titulo);
        titleRow.getCell(0).setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        sheet.createRow(row++).createCell(0).setCellValue("Filtro: " + filtro);
        row++;

        Row h = sheet.createRow(row++);
        for (int i = 0; i < headers.length; i++) {
            h.createCell(i).setCellValue(headers[i]);
            h.getCell(i).setCellStyle(headerStyle);
        }
        return row;
    }

    private void crearCeldaMoneda(Row row, int columna, Double valor, CellStyle moneyStyle) {
        row.createCell(columna).setCellValue(val(valor));
        row.getCell(columna).setCellStyle(moneyStyle);
    }

    private String describirEstado(String estadoPago) {
        if (estadoPago == null) {
            return "";
        }
        return switch (estadoPago) {
            case "EFECTIVO" -> "Pagada - Efectivo";
            case "BANCO" -> "Pagada - Banco";
            case "VENCIDA" -> "Vencida";
            default -> "Pendiente";
        };
    }

    private double val(Double v) {
        return v != null ? v : 0.0;
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
