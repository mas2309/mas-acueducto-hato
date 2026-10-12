package com.mas.co.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.mas.co.entity.Cuota;
import com.mas.co.entity.Factura;
import com.mas.co.entity.FacturaCuota;
import com.mas.co.entity.Usuario;
import com.mas.co.repository.FacturaRepository;
import com.mas.co.service.impl.FacturaPdfService.Concepto;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para FacturaPdfService")
class FacturaPdfServiceTest {

  @Mock private FacturaRepository facturaRepository;

  @InjectMocks private FacturaPdfService facturaPdfService;

  private Factura factura;

  @BeforeEach
  void setUp() {
    Usuario usuario = Usuario.builder().id(1L).nombre("Juan").apellidos("Pérez").build();

    factura =
        Factura.builder()
            .id(1L)
            .usuario(usuario)
            .mes("Enero")
            .anio(2026)
            .lecturaActual(120)
            .lecturaAnterior(100)
            .consumo(20)
            .cargoFijo(15000.0)
            .valorConsumo(30000.0)
            .valorTotal(45000.0)
            .build();
  }

  @Test
  @DisplayName("Debe mostrar solo cargo fijo y consumo si no hay más cobros")
  void debeMostrarSoloCargoFijoYConsumo() {
    List<Concepto> conceptos = facturaPdfService.buildConceptos(factura);

    assertEquals(
        List.of(new Concepto("CARGO FIJO", 15000.0), new Concepto("CONSUMO", 30000.0)), conceptos);
  }

  @Test
  @DisplayName("Debe mostrar cada cuota en su propia fila")
  void debeMostrarCadaCuotaEnSuFila() {
    factura.setDeudaAnterior(5000.0);
    factura.setOtrosCobros(8000.0);
    factura.setOtrosCobrosDescripcion("Reconexión");
    factura.setNoPago(2000.0);
    factura.agregarCuota(cuota("Financiación", 6, 12, 25000.0));
    factura.agregarCuota(cuota("Obras tanque", 1, 2, 50000.0));

    List<Concepto> conceptos = facturaPdfService.buildConceptos(factura);

    assertEquals(
        List.of(
            new Concepto("CARGO FIJO", 15000.0),
            new Concepto("CONSUMO", 30000.0),
            new Concepto("DEUDA ANTERIOR", 5000.0),
            new Concepto("Financiación (cuota 6 de 12)", 25000.0),
            new Concepto("Obras tanque (cuota 1 de 2)", 50000.0),
            new Concepto("Reconexión", 8000.0),
            new Concepto("No Pago", 2000.0)),
        conceptos);
  }

  @Test
  @DisplayName("Debe agrupar las cuotas que no caben en la factura")
  void debeAgruparCuotasQueNoCaben() {
    factura.setDeudaAnterior(5000.0);
    factura.setOtrosCobros(8000.0);
    factura.setNoPago(2000.0);
    factura.agregarCuota(cuota("Cuota A", 1, 2, 1000.0));
    factura.agregarCuota(cuota("Cuota B", 1, 2, 2000.0));
    factura.agregarCuota(cuota("Cuota C", 1, 2, 3000.0));
    factura.agregarCuota(cuota("Cuota D", 1, 2, 4000.0));

    List<Concepto> conceptos = facturaPdfService.buildConceptos(factura);

    assertEquals(8, conceptos.size());
    assertEquals(new Concepto("Cuota A (cuota 1 de 2)", 1000.0), conceptos.get(3));
    assertEquals(new Concepto("Cuota B (cuota 1 de 2)", 2000.0), conceptos.get(4));
    assertEquals(new Concepto("OTRAS CUOTAS (2)", 7000.0), conceptos.get(5));
    assertEquals(new Concepto("OTROS COBROS", 8000.0), conceptos.get(6));
  }

  @Test
  @DisplayName("Debe usar la cuota actual en facturas sin número de cuota guardado")
  void debeUsarCuotaActualSiNoHayNumeroGuardado() {
    FacturaCuota cuotaAntigua = cuota("Financiación", 6, 12, 25000.0);
    cuotaAntigua.setNumeroCuota(null);
    cuotaAntigua.getCuota().setCuotaActual(9);
    factura.agregarCuota(cuotaAntigua);

    List<Concepto> conceptos = facturaPdfService.buildConceptos(factura);

    assertEquals(new Concepto("Financiación (cuota 9 de 12)", 25000.0), conceptos.get(2));
  }

  @Test
  @DisplayName("Debe generar el PDF de una factura con varias cuotas")
  void debeGenerarPdfConVariasCuotas() {
    factura.agregarCuota(cuota("Financiación", 6, 12, 25000.0));
    factura.agregarCuota(cuota("Obras tanque", 1, 2, 50000.0));
    when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(List.of());

    byte[] pdf = facturaPdfService.generarPdf(1L);

    assertTrue(new String(pdf, StandardCharsets.ISO_8859_1).startsWith("%PDF"));
  }

  private FacturaCuota cuota(String descripcion, int numeroCuota, int totalCuotas, double valor) {
    return FacturaCuota.builder()
        .cuota(new Cuota())
        .descripcion(descripcion)
        .numeroCuota(numeroCuota)
        .totalCuotas(totalCuotas)
        .valor(valor)
        .build();
  }
}
