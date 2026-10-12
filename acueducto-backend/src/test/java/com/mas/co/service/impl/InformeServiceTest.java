package com.mas.co.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mas.co.dto.InformeDto;
import com.mas.co.dto.InformeDto.DeudorDto;
import com.mas.co.dto.InformeDto.RangoConsumoDto;
import com.mas.co.entity.Factura;
import com.mas.co.entity.Usuario;
import com.mas.co.entity.Valores;
import com.mas.co.repository.FacturaRepository;
import com.mas.co.service.ValorService;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para InformeService")
class InformeServiceTest {

  @Mock private FacturaRepository facturaRepository;

  @Mock private EstadoFacturaService estadoFacturaService;

  @Mock private ValorService valorService;

  @InjectMocks private InformeService informeService;

  private Usuario ana;
  private Usuario beto;
  private Usuario carla;
  private Usuario dario;

  @BeforeEach
  void setUp() {
    ana = Usuario.builder().id(1L).nombre("Ana").apellidos("Rojas").build();
    beto = Usuario.builder().id(2L).nombre("Beto").apellidos("Díaz").activo(false).build();
    carla = Usuario.builder().id(3L).nombre("Carla").apellidos("Mora").build();
    dario = Usuario.builder().id(4L).nombre("Darío").apellidos("Peña").build();

    Valores valores = new Valores();
    valores.setRangoUno(10.0);
    valores.setRangoDos(20.0);
    valores.setRangoTres(30.0);
    valores.setValorUno(600);
    valores.setValorDos(1000);
    valores.setValorTres(1500);
    valores.setValorCuatro(3500);
    when(valorService.obtenerValores()).thenReturn(valores);

    when(facturaRepository.findByAnio(2026)).thenReturn(List.of());
  }

  @Test
  @DisplayName("Debe agrupar las facturas del período por rango de consumo")
  void debeAgruparFacturasPorRangoDeConsumo() {
    facturasPagadasEnEfectivo();
    when(facturaRepository.findByAnio(2026))
        .thenReturn(
            List.of(
                consumo(ana, "Enero", 5, 3000.0),
                consumo(ana, "Febrero", 10, 6000.0),
                consumo(beto, "Enero", 11, 7000.0),
                consumo(carla, "Enero", 30, 31000.0),
                consumo(dario, "Enero", 40, 66000.0),
                consumo(dario, "Febrero", 0, 0.0)));
    when(facturaRepository.findUltimasFacturasPorUsuario()).thenReturn(List.of());

    List<RangoConsumoDto> rangos = informeService.generarInformeAnual(2026).getRangosConsumo();

    assertEquals(
        List.of("Sin consumo", "Rango 1", "Rango 2", "Rango 3", "Rango 4"),
        rangos.stream().map(RangoConsumoDto::getNombre).toList());
    assertEquals(
        List.of("0 m³", "1 a 10 m³", "11 a 20 m³", "21 a 30 m³", "Más de 30 m³"),
        rangos.stream().map(RangoConsumoDto::getDescripcion).toList());

    RangoConsumoDto rangoUno = rangos.get(1);
    assertEquals(600, rangoUno.getValorMetroCubico());
    assertEquals(1, rangoUno.getUsuarios());
    assertEquals(2, rangoUno.getFacturas());
    assertEquals(15, rangoUno.getConsumoM3());
    assertEquals(9000.0, rangoUno.getValorConsumo());
    assertEquals(33, rangoUno.getPorcentajeFacturas());
    assertEquals(8, rangoUno.getPorcentajeValor());

    assertEquals(List.of(1, 2, 1, 1, 1), rangos.stream().map(RangoConsumoDto::getFacturas).toList());
    assertEquals(66000.0, rangos.get(4).getValorConsumo());
    assertEquals(58, rangos.get(4).getPorcentajeValor());
  }

  @Test
  @DisplayName("No debe mostrar el rango sin consumo si no hay facturas en cero")
  void noDebeMostrarSinConsumoSiNoHayFacturasEnCero() {
    facturasPagadasEnEfectivo();
    when(facturaRepository.findByAnio(2026)).thenReturn(List.of(consumo(ana, "Enero", 5, 3000.0)));
    when(facturaRepository.findUltimasFacturasPorUsuario()).thenReturn(List.of());

    List<RangoConsumoDto> rangos = informeService.generarInformeAnual(2026).getRangosConsumo();

    assertEquals(
        List.of("Rango 1", "Rango 2", "Rango 3", "Rango 4"),
        rangos.stream().map(RangoConsumoDto::getNombre).toList());
  }

  private void facturasPagadasEnEfectivo() {
    when(estadoFacturaService.calcularEstado(anyBoolean(), anyBoolean(), anyString(), anyInt()))
        .thenReturn("EFECTIVO");
  }

  private Factura consumo(Usuario usuario, String mes, int consumo, double valorConsumo) {
    return Factura.builder()
        .usuario(usuario)
        .mes(mes)
        .anio(2026)
        .consumo(consumo)
        .valorConsumo(valorConsumo)
        .valorTotal(valorConsumo)
        .pago(true)
        .build();
  }

  @Test
  @DisplayName("Debe listar los deudores del que más debe al que menos")
  void debeListarDeudoresDeMayorAMenor() {
    // Ana pagó enero y debe tres facturas seguidas
    Factura anaAbril = factura(ana, "Abril", 2026, 90000.0, null);
    List<Factura> deAna =
        List.of(
            factura(ana, "Enero", 2026, 30000.0, LocalDate.of(2026, 2, 10)),
            factura(ana, "Febrero", 2026, 30000.0, null),
            factura(ana, "Marzo", 2026, 60000.0, null),
            anaAbril);
    // Beto nunca ha pagado
    Factura betoAbril = factura(beto, "Abril", 2026, 150000.0, null);
    List<Factura> deBeto = List.of(factura(beto, "Marzo", 2026, 70000.0, null), betoAbril);
    // Carla solo tiene pendiente la factura que aún está en plazo
    Factura carlaAbril = factura(carla, "Abril", 2026, 30000.0, null);
    // Darío saldó diciembre al pagar enero (sin fecha de pago) y volvió a deber
    Factura darioFebrero = factura(dario, "Febrero", 2026, 20000.0, null);
    Factura darioEnero = factura(dario, "Enero", 2026, 55000.0, null);
    darioEnero.setPagoBanco(true);
    List<Factura> deDario =
        List.of(darioFebrero, factura(dario, "Diciembre", 2025, 25000.0, null), darioEnero);

    when(facturaRepository.findUltimasFacturasPorUsuario())
        .thenReturn(List.of(anaAbril, betoAbril, carlaAbril, darioFebrero));
    when(facturaRepository.findByUsuarioIdIn(Set.of(1L, 2L, 3L, 4L)))
        .thenReturn(concatenar(deAna, deBeto, List.of(carlaAbril), deDario));
    when(estadoFacturaService.estaVencida(anyString(), anyInt()))
        .thenAnswer(invocation -> !"Abril".equals(invocation.getArgument(0)));

    InformeDto informe = informeService.generarInformeAnual(2026);

    List<DeudorDto> deudores = informe.getDeudores();
    assertEquals(
        List.of("Beto Díaz", "Ana Rojas", "Darío Peña"),
        deudores.stream().map(DeudorDto::getUsuarioNombre).toList());

    DeudorDto deudorBeto = deudores.get(0);
    assertEquals(150000.0, deudorBeto.getDeuda());
    assertEquals(2, deudorBeto.getFacturasSinPagar());
    assertEquals("Marzo 2026", deudorBeto.getDebeDesde());
    assertNull(deudorBeto.getUltimoPago());
    assertFalse(deudorBeto.isUsuarioActivo());

    DeudorDto deudorAna = deudores.get(1);
    assertEquals(1L, deudorAna.getUsuarioId());
    assertEquals(90000.0, deudorAna.getDeuda());
    assertEquals(3, deudorAna.getFacturasSinPagar());
    assertEquals("Febrero 2026", deudorAna.getDebeDesde());
    assertEquals("2026-02-10", deudorAna.getUltimoPago());
    assertTrue(deudorAna.isUsuarioActivo());

    DeudorDto deudorDario = deudores.get(2);
    assertEquals(20000.0, deudorDario.getDeuda());
    assertEquals(1, deudorDario.getFacturasSinPagar());
    assertEquals("Febrero 2026", deudorDario.getDebeDesde());
    assertEquals("Enero 2026", deudorDario.getUltimoPago());

    assertEquals(290000.0, informe.getTotalDeudaPendienteReal());
  }

  @Test
  @DisplayName("No debe consultar facturas si nadie debe")
  void noDebeConsultarFacturasSiNadieDebe() {
    Factura pagada = factura(ana, "Abril", 2026, 30000.0, LocalDate.of(2026, 5, 2));
    when(facturaRepository.findUltimasFacturasPorUsuario()).thenReturn(List.of(pagada));

    InformeDto informe = informeService.generarInformeAnual(2026);

    assertTrue(informe.getDeudores().isEmpty());
    assertEquals(0.0, informe.getTotalDeudaPendienteReal());
    verify(facturaRepository, never()).findByUsuarioIdIn(any());
  }

  private Factura factura(
      Usuario usuario, String mes, int anio, double valorTotal, LocalDate fechaPago) {
    return Factura.builder()
        .usuario(usuario)
        .mes(mes)
        .anio(anio)
        .valorTotal(valorTotal)
        .pago(fechaPago != null)
        .fechaPago(fechaPago)
        .build();
  }

  @SafeVarargs
  private List<Factura> concatenar(List<Factura>... listas) {
    return List.of(listas).stream().flatMap(List::stream).toList();
  }
}
