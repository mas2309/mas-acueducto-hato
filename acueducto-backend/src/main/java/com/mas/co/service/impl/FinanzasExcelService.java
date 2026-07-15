package com.mas.co.service.impl;

import com.mas.co.dto.GastoDto;
import com.mas.co.dto.IngresoDto;
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
public class FinanzasExcelService {

    public byte[] generarExcelIngresos(List<IngresoDto> ingresos, Integer anio) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle headerStyle = crearEstiloHeader(wb);
            CellStyle moneyStyle = crearEstiloMoneda(wb);
            CellStyle titleStyle = crearEstiloTitulo(wb);

            Sheet sheet = wb.createSheet("Ingresos " + anio);
            int row = 0;

            Row titleRow = sheet.createRow(row++);
            titleRow.createCell(0).setCellValue("INGRESOS - ACUEDUCTO EL HATO - " + anio);
            titleRow.getCell(0).setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
            row++;

            Row h = sheet.createRow(row++);
            String[] headers = {"ID", "Fecha", "Descripción", "Categoría", "Monto ($)"};
            for (int i = 0; i < headers.length; i++) {
                h.createCell(i).setCellValue(headers[i]);
                h.getCell(i).setCellStyle(headerStyle);
            }

            double total = 0;
            for (IngresoDto ing : ingresos) {
                Row r = sheet.createRow(row++);
                r.createCell(0).setCellValue(ing.getId());
                r.createCell(1).setCellValue(ing.getFecha() != null ? ing.getFecha().toString() : "");
                r.createCell(2).setCellValue(ing.getDescripcion());
                r.createCell(3).setCellValue(ing.getCategoria() != null ? ing.getCategoria().name() : "");
                r.createCell(4).setCellValue(ing.getMonto());
                r.getCell(4).setCellStyle(moneyStyle);
                total += ing.getMonto() != null ? ing.getMonto() : 0;
            }

            row++;
            Row totalRow = sheet.createRow(row);
            totalRow.createCell(3).setCellValue("TOTAL:");
            totalRow.getCell(3).setCellStyle(headerStyle);
            totalRow.createCell(4).setCellValue(total);
            totalRow.getCell(4).setCellStyle(moneyStyle);

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generarExcelGastos(List<GastoDto> gastos, Integer anio) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle headerStyle = crearEstiloHeader(wb);
            CellStyle moneyStyle = crearEstiloMoneda(wb);
            CellStyle titleStyle = crearEstiloTitulo(wb);

            Sheet sheet = wb.createSheet("Gastos " + anio);
            int row = 0;

            Row titleRow = sheet.createRow(row++);
            titleRow.createCell(0).setCellValue("GASTOS - ACUEDUCTO EL HATO - " + anio);
            titleRow.getCell(0).setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
            row++;

            Row h = sheet.createRow(row++);
            String[] headers = {"ID", "Fecha", "Descripción", "Categoría", "Responsable", "Estado", "Monto ($)"};
            for (int i = 0; i < headers.length; i++) {
                h.createCell(i).setCellValue(headers[i]);
                h.getCell(i).setCellStyle(headerStyle);
            }

            double total = 0;
            for (GastoDto g : gastos) {
                Row r = sheet.createRow(row++);
                r.createCell(0).setCellValue(g.getId());
                r.createCell(1).setCellValue(g.getFecha() != null ? g.getFecha().toString() : "");
                r.createCell(2).setCellValue(g.getDescripcion());
                r.createCell(3).setCellValue(g.getCategoria() != null ? g.getCategoria().name() : "");
                r.createCell(4).setCellValue(g.getResponsable() != null ? g.getResponsable() : "");
                r.createCell(5).setCellValue(Boolean.TRUE.equals(g.getPagado()) ? "Pagado" : "Pendiente");
                r.createCell(6).setCellValue(g.getMonto());
                r.getCell(6).setCellStyle(moneyStyle);
                total += g.getMonto() != null ? g.getMonto() : 0;
            }

            row++;
            Row totalRow = sheet.createRow(row);
            totalRow.createCell(5).setCellValue("TOTAL:");
            totalRow.getCell(5).setCellStyle(headerStyle);
            totalRow.createCell(6).setCellValue(total);
            totalRow.getCell(6).setCellStyle(moneyStyle);

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(out);
            return out.toByteArray();
        }
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
