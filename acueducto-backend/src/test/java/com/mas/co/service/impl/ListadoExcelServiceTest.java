package com.mas.co.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mas.co.dto.LecturaDto;
import com.mas.co.dto.UsuarioDto;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests para ListadoExcelService")
class ListadoExcelServiceTest {

  private static final int FILA_ENCABEZADOS = 3;

  private final ListadoExcelService listadoExcelService = new ListadoExcelService();

  @Test
  @DisplayName("Debe generar el Excel de usuarios con una fila por usuario")
  void debeGenerarExcelDeUsuarios() throws IOException {
    List<UsuarioDto> usuarios =
        List.of(
            UsuarioDto.builder()
                .id(1L)
                .nombre("Ana")
                .apellidos("Rojas")
                .fechaInsert(LocalDate.of(2024, 1, 15))
                .activo(true)
                .build(),
            UsuarioDto.builder().id(2L).nombre("Beto").apellidos("Díaz").activo(false).build());

    byte[] excel = listadoExcelService.generarExcelUsuarios(usuarios, "Usuarios activos");

    try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
      Sheet sheet = wb.getSheet("Usuarios");
      assertEquals("Filtro: Usuarios activos", sheet.getRow(1).getCell(0).getStringCellValue());
      assertEquals("Nombres", sheet.getRow(FILA_ENCABEZADOS).getCell(1).getStringCellValue());

      Row ana = sheet.getRow(FILA_ENCABEZADOS + 1);
      assertEquals(1.0, ana.getCell(0).getNumericCellValue());
      assertEquals("Ana", ana.getCell(1).getStringCellValue());
      assertEquals("Rojas", ana.getCell(2).getStringCellValue());
      assertEquals("2024-01-15", ana.getCell(3).getStringCellValue());
      assertEquals("Activo", ana.getCell(4).getStringCellValue());

      Row beto = sheet.getRow(FILA_ENCABEZADOS + 2);
      assertEquals("", beto.getCell(3).getStringCellValue());
      assertEquals("Inactivo", beto.getCell(4).getStringCellValue());
      assertEquals(FILA_ENCABEZADOS + 2, sheet.getLastRowNum());
    }
  }

  @Test
  @DisplayName("Debe generar el Excel de facturas con desglose y total")
  void debeGenerarExcelDeFacturas() throws IOException {
    List<LecturaDto> facturas =
        List.of(
            factura(10L, "Enero", 45000.0, "EFECTIVO"),
            factura(11L, "Febrero", 55000.0, "VENCIDA"),
            factura(12L, "Marzo", 20000.0, "NO_PAGO"));

    byte[] excel = listadoExcelService.generarExcelFacturas(facturas, "Facturas del año 2026");

    try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
      Sheet sheet = wb.getSheet("Facturas");
      assertEquals("Filtro: Facturas del año 2026", sheet.getRow(1).getCell(0).getStringCellValue());
      assertEquals("Total ($)", sheet.getRow(FILA_ENCABEZADOS).getCell(15).getStringCellValue());

      Row enero = sheet.getRow(FILA_ENCABEZADOS + 1);
      assertEquals(10.0, enero.getCell(0).getNumericCellValue());
      assertEquals("Ana Rojas", enero.getCell(2).getStringCellValue());
      assertEquals("Enero", enero.getCell(3).getStringCellValue());
      assertEquals(20.0, enero.getCell(7).getNumericCellValue());
      assertEquals(25000.0, enero.getCell(10).getNumericCellValue());
      assertEquals("", enero.getCell(12).getStringCellValue());
      assertEquals(0.0, enero.getCell(13).getNumericCellValue());
      assertEquals(45000.0, enero.getCell(15).getNumericCellValue());
      assertEquals("Pagada - Efectivo", enero.getCell(16).getStringCellValue());
      assertEquals("2026-01-15", enero.getCell(17).getStringCellValue());

      assertEquals("Vencida", sheet.getRow(FILA_ENCABEZADOS + 2).getCell(16).getStringCellValue());
      assertEquals("Pendiente", sheet.getRow(FILA_ENCABEZADOS + 3).getCell(16).getStringCellValue());

      Row total = sheet.getRow(FILA_ENCABEZADOS + 5);
      assertEquals("TOTAL:", total.getCell(14).getStringCellValue());
      assertEquals(120000.0, total.getCell(15).getNumericCellValue());
    }
  }

  private LecturaDto factura(Long id, String mes, double valorTotal, String estadoPago) {
    return LecturaDto.builder()
        .id(id)
        .usuarioId(1L)
        .usuarioNombre("Ana Rojas")
        .mes(mes)
        .anio(2026)
        .lecturaAnterior(100)
        .lecturaActual(120)
        .consumo(20)
        .valorConsumo(17000.0)
        .cargoFijo(3000.0)
        .valorCuota(25000.0)
        .valorTotal(valorTotal)
        .estadoPago(estadoPago)
        .fechaIngreso(LocalDate.of(2026, 1, 15))
        .build();
  }
}
