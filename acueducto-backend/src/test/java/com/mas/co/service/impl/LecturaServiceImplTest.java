package com.mas.co.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mas.co.dto.LecturaDto;
import com.mas.co.dto.PagoDto;
import com.mas.co.entity.Cuota;
import com.mas.co.entity.Factura;
import com.mas.co.entity.Usuario;
import com.mas.co.exception.BusinessException;
import com.mas.co.mapper.LecturaMapper;
import com.mas.co.repository.CuotaRepository;
import com.mas.co.repository.FacturaRepository;
import com.mas.co.repository.UsuarioRepository;
import com.mas.co.service.CuotaService;
import com.mas.co.usecase.CalcularValoresFacturaUseCase;
import com.mas.co.usecase.ObtenerDeudaAnteriorUseCase;
import com.mas.co.usecase.ObtenerLecturaAnteriorUseCase;
import com.mas.co.usecase.ValidarPeriodoConsecutivoUseCase;
import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("LecturaServiceImpl - Tests Completos")
class LecturaServiceImplTest {

  @Mock private FacturaRepository facturaRepository;
  @Mock private UsuarioRepository usuarioRepository;
  @Mock private CuotaRepository cuotaRepository;
  @Mock private LecturaMapper lecturaMapper;
  @Mock private CuotaService cuotaService;
  @Mock private EntityManager entityManager;
  @Mock private ValidarPeriodoConsecutivoUseCase validarPeriodoConsecutivoUseCase;
  @Mock private ObtenerLecturaAnteriorUseCase obtenerLecturaAnteriorUseCase;
  @Mock private ObtenerDeudaAnteriorUseCase obtenerDeudaAnteriorUseCase;
  @Mock private CalcularValoresFacturaUseCase calcularValoresFacturaUseCase;

  @InjectMocks private LecturaServiceImpl lecturaService;

  private Usuario usuario;
  private Factura factura;
  private LecturaDto lecturaDto;
  private Cuota cuota;

  @BeforeEach
  void setUp() {
    usuario = new Usuario();
    usuario.setId(1L);
    usuario.setNombre("Test");
    usuario.setApellidos("Usuario");

    factura = new Factura();
    factura.setId(1L);
    factura.setUsuario(usuario);
    factura.setMes("Enero");
    factura.setAnio(2024);
    factura.setLecturaActual(100);
    factura.setLecturaAnterior(50);
    factura.setPago(false);
    factura.setPagoBanco(false);

    lecturaDto = LecturaDto.builder()
        .usuarioId(1L)
        .mes("Enero")
        .anio(2024)
        .lecturaActual(100)
        .lecturaAnterior(50)
        .consumo(50)
        .valorConsumo(10000.0)
        .cargoFijo(3000.0)
        .valorTotal(13000.0)
        .build();

    cuota = new Cuota();
    cuota.setId(1L);
    cuota.setCuotaActual(5);
    cuota.setValorCuota(25000.0);
    cuota.setActivo(true);
  }

  @Test
  @DisplayName("Ingresar lectura - Success sin cuota")
  void ingresarLectura_SinCuota_Success() {
    when(usuarioRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(usuario));
    doNothing().when(validarPeriodoConsecutivoUseCase).execute(any());
    when(facturaRepository.existsByUsuarioIdAndMesAndAnio(1L, "Enero", 2024)).thenReturn(false);
    when(obtenerLecturaAnteriorUseCase.execute(1L)).thenReturn(50);
    when(obtenerDeudaAnteriorUseCase.execute(1L)).thenReturn(0.0);
    when(cuotaRepository.findFirstByUsuarioIdAndActivoTrueOrderByFechaInsertDesc(1L))
        .thenReturn(Optional.empty());
    doNothing().when(calcularValoresFacturaUseCase).execute(any());
    when(lecturaMapper.toEntity(any())).thenReturn(factura);
    when(facturaRepository.save(any())).thenReturn(factura);
    when(lecturaMapper.toDto(any())).thenReturn(lecturaDto);

    LecturaDto result = lecturaService.ingresarLectura(lecturaDto);

    assertNotNull(result);
    verify(facturaRepository).save(any());
  }

  @Test
  @DisplayName("Ingresar lectura - Success con cuota")
  void ingresarLectura_ConCuota_Success() {
    when(usuarioRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(usuario));
    doNothing().when(validarPeriodoConsecutivoUseCase).execute(any());
    when(facturaRepository.existsByUsuarioIdAndMesAndAnio(1L, "Enero", 2024)).thenReturn(false);
    when(obtenerLecturaAnteriorUseCase.execute(1L)).thenReturn(50);
    when(obtenerDeudaAnteriorUseCase.execute(1L)).thenReturn(0.0);
    when(cuotaRepository.findFirstByUsuarioIdAndActivoTrueOrderByFechaInsertDesc(1L))
        .thenReturn(Optional.of(cuota));
    doNothing().when(calcularValoresFacturaUseCase).execute(any());
    when(lecturaMapper.toEntity(any())).thenReturn(factura);
    when(facturaRepository.save(any())).thenReturn(factura);
    when(lecturaMapper.toDto(any())).thenReturn(lecturaDto);

    LecturaDto result = lecturaService.ingresarLectura(lecturaDto);

    assertNotNull(result);
    verify(cuotaService).procesarPagoCuota(1L);
  }

  @Test
  @DisplayName("Ingresar lectura - Usuario no encontrado")
  void ingresarLectura_UsuarioNoEncontrado_ThrowsException() {
    when(usuarioRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> lecturaService.ingresarLectura(lecturaDto));
    
    verify(facturaRepository, never()).save(any());
  }

  @Test
  @DisplayName("Ingresar lectura - Factura duplicada")
  void ingresarLectura_FacturaDuplicada_ThrowsException() {
    when(usuarioRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(usuario));
    doNothing().when(validarPeriodoConsecutivoUseCase).execute(any());
    when(facturaRepository.existsByUsuarioIdAndMesAndAnio(1L, "Enero", 2024)).thenReturn(true);

    assertThrows(BusinessException.class, () -> lecturaService.ingresarLectura(lecturaDto));
  }

  @Test
  @DisplayName("Ingresar lectura - Lectura inválida")
  void ingresarLectura_LecturaInvalida_ThrowsException() {
    lecturaDto.setLecturaActual(30);
    when(usuarioRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(usuario));
    doNothing().when(validarPeriodoConsecutivoUseCase).execute(any());
    when(facturaRepository.existsByUsuarioIdAndMesAndAnio(1L, "Enero", 2024)).thenReturn(false);
    when(obtenerLecturaAnteriorUseCase.execute(1L)).thenReturn(50);

    assertThrows(BusinessException.class, () -> lecturaService.ingresarLectura(lecturaDto));
  }

  @Test
  @DisplayName("Registrar pago - Success efectivo")
  void registrarPago_Efectivo_Success() {
    PagoDto pagoDto = new PagoDto();
    pagoDto.setMetodoPago("EFECTIVO");
    
    when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
    when(facturaRepository.save(any())).thenReturn(factura);
    when(lecturaMapper.toDto(any())).thenReturn(lecturaDto);

    LecturaDto result = lecturaService.registrarPago(1L, pagoDto);

    assertNotNull(result);
    verify(facturaRepository).save(any());
  }

  @Test
  @DisplayName("Registrar pago - Success banco")
  void registrarPago_Banco_Success() {
    PagoDto pagoDto = new PagoDto();
    pagoDto.setMetodoPago("BANCO");
    
    when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));
    when(facturaRepository.save(any())).thenReturn(factura);
    when(lecturaMapper.toDto(any())).thenReturn(lecturaDto);

    LecturaDto result = lecturaService.registrarPago(1L, pagoDto);

    assertNotNull(result);
    verify(facturaRepository).save(any());
  }

  @Test
  @DisplayName("Registrar pago - Factura no encontrada")
  void registrarPago_FacturaNoEncontrada_ThrowsException() {
    PagoDto pagoDto = new PagoDto();
    pagoDto.setMetodoPago("EFECTIVO");
    
    when(facturaRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> lecturaService.registrarPago(1L, pagoDto));
  }

  @Test
  @DisplayName("Registrar pago - Factura ya pagada")
  void registrarPago_FacturaYaPagada_ThrowsException() {
    factura.setPago(true);
    PagoDto pagoDto = new PagoDto();
    pagoDto.setMetodoPago("EFECTIVO");
    
    when(facturaRepository.findById(1L)).thenReturn(Optional.of(factura));

    assertThrows(BusinessException.class, () -> lecturaService.registrarPago(1L, pagoDto));
  }

  @Test
  @DisplayName("Obtener última lectura - Con facturas")
  void obtenerUltimaLectura_ConFacturas_Success() {
    when(facturaRepository.findUltimaLecturaPorUsuario(1L))
        .thenReturn(Collections.singletonList(factura));
    when(lecturaMapper.toDto(factura)).thenReturn(lecturaDto);

    LecturaDto result = lecturaService.obtenerUltimaLectura(1L);

    assertNotNull(result);
    verify(facturaRepository).findUltimaLecturaPorUsuario(1L);
  }

  @Test
  @DisplayName("Obtener última lectura - Sin facturas")
  void obtenerUltimaLectura_SinFacturas_ReturnsDefault() {
    when(facturaRepository.findUltimaLecturaPorUsuario(1L)).thenReturn(Collections.emptyList());

    LecturaDto result = lecturaService.obtenerUltimaLectura(1L);

    assertNotNull(result);
    assertEquals(1L, result.getUsuarioId());
    assertEquals(0, result.getLecturaAnterior());
  }

  @Test
  @DisplayName("Obtener facturas por usuario - Success")
  void obtenerFacturasPorUsuario_Success() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Factura> page = new PageImpl<>(Collections.singletonList(factura));
    
    when(facturaRepository.findByUsuarioIdOrderByAnioDescMesDesc(1L, pageable)).thenReturn(page);
    when(lecturaMapper.toDto(factura)).thenReturn(lecturaDto);

    Page<LecturaDto> result = lecturaService.obtenerFacturasPorUsuario(1L, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  @DisplayName("Obtener facturas por período - Success")
  void obtenerFacturasPorPeriodo_Success() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Factura> page = new PageImpl<>(Collections.singletonList(factura));
    
    when(facturaRepository.findByMesAndAnioOrderByFechaIngresoDesc("Enero", 2024, pageable))
        .thenReturn(page);
    when(lecturaMapper.toDto(factura)).thenReturn(lecturaDto);

    Page<LecturaDto> result = lecturaService.obtenerFacturasPorPeriodo("Enero", 2024, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  @DisplayName("Obtener facturas pendientes - Success")
  void obtenerFacturasPendientes_Success() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Factura> page = new PageImpl<>(Collections.singletonList(factura));
    
    when(facturaRepository.findFacturasPendientes(pageable)).thenReturn(page);
    when(lecturaMapper.toDto(factura)).thenReturn(lecturaDto);

    Page<LecturaDto> result = lecturaService.obtenerFacturasPendientes(pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  @DisplayName("Obtener facturas pagadas - Success")
  void obtenerFacturasPagadas_Success() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Factura> page = new PageImpl<>(Collections.singletonList(factura));
    
    when(facturaRepository.findFacturasPagadas(pageable)).thenReturn(page);
    when(lecturaMapper.toDto(factura)).thenReturn(lecturaDto);

    Page<LecturaDto> result = lecturaService.obtenerFacturasPagadas(pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  @DisplayName("Buscar facturas - Success")
  void buscarFacturas_Success() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Factura> page = new PageImpl<>(Collections.singletonList(factura));
    
    when(facturaRepository.findByUsuarioNombreContaining("Test", pageable)).thenReturn(page);
    when(lecturaMapper.toDto(factura)).thenReturn(lecturaDto);

    Page<LecturaDto> result = lecturaService.buscarFacturas("Test", pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  @DisplayName("Obtener deuda pendiente - Success")
  void obtenerDeudaPendiente_Success() {
    when(facturaRepository.findDeudaPendientePorUsuario(1L)).thenReturn(50000.0);

    Double result = lecturaService.obtenerDeudaPendiente(1L);

    assertEquals(50000.0, result);
    verify(facturaRepository).findDeudaPendientePorUsuario(1L);
  }
}
