package com.mas.co.usecase;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mas.co.dto.LecturaDto;
import com.mas.co.entity.Valores;
import com.mas.co.service.ValorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para CalcularValoresFacturaUseCase")
class CalcularValoresFacturaUseCaseTest {

    @Mock
    private ValorService valorService;

    @Mock
    private VerificarPagoLecturaAnteriorUseCase verificarPagoLecturaAnteriorUseCase;

    @InjectMocks
    private CalcularValoresFacturaUseCase useCase;

    private Valores valores;

    @BeforeEach
    void setUp() {
        valores = new Valores();
        valores.setCargoFijo(5000.0);
        valores.setNoPago(2000.0);
    }

    @Test
    @DisplayName("Debe calcular valores correctamente con lectura anterior pagada")
    void debeCalcularValoresConLecturaAnteriorPagada() {
        LecturaDto dto = new LecturaDto();
        dto.setUsuarioId(1L);
        dto.setConsumo(10);
        dto.setValorCuota(0.0);

        when(valorService.calcularValorConsumo(10)).thenReturn(15000.0);
        when(valorService.obtenerValores()).thenReturn(valores);
        when(verificarPagoLecturaAnteriorUseCase.execute(1L)).thenReturn(true);

        useCase.execute(dto);

        assertEquals(15000.0, dto.getValorConsumo());
        assertEquals(5000.0, dto.getCargoFijo());
        assertEquals(0.0, dto.getOtrosCobros());
        assertEquals(0.0, dto.getDeudaAnterior());
        assertEquals(0.0, dto.getNoPago());
        assertEquals(20000.0, dto.getValorTotal());
    }

    @Test
    @DisplayName("Debe aplicar valor de no pago cuando lectura anterior no fue pagada")
    void debeAplicarNoPagoCuandoLecturaAnteriorNoPagada() {
        LecturaDto dto = new LecturaDto();
        dto.setUsuarioId(1L);
        dto.setConsumo(10);
        dto.setValorCuota(0.0);

        when(valorService.calcularValorConsumo(10)).thenReturn(15000.0);
        when(valorService.obtenerValores()).thenReturn(valores);
        when(verificarPagoLecturaAnteriorUseCase.execute(1L)).thenReturn(false);

        useCase.execute(dto);

        assertEquals(2000.0, dto.getNoPago());
        assertEquals(22000.0, dto.getValorTotal());
    }

    @Test
    @DisplayName("Debe usar cargo fijo existente si ya está definido")
    void debeUsarCargoFijoExistente() {
        LecturaDto dto = new LecturaDto();
        dto.setUsuarioId(1L);
        dto.setConsumo(10);
        dto.setCargoFijo(7000.0);
        dto.setValorCuota(0.0);

        when(valorService.calcularValorConsumo(10)).thenReturn(15000.0);
        when(verificarPagoLecturaAnteriorUseCase.execute(1L)).thenReturn(true);

        useCase.execute(dto);

        assertEquals(7000.0, dto.getCargoFijo());
        assertEquals(22000.0, dto.getValorTotal());
        verify(valorService, never()).obtenerValores();
    }

    @Test
    @DisplayName("Debe inicializar otros cobros en cero si es null")
    void debeInicializarOtrosCobrosEnCero() {
        LecturaDto dto = new LecturaDto();
        dto.setUsuarioId(1L);
        dto.setConsumo(10);
        dto.setValorCuota(0.0);
        dto.setOtrosCobros(null);

        when(valorService.calcularValorConsumo(10)).thenReturn(15000.0);
        when(valorService.obtenerValores()).thenReturn(valores);
        when(verificarPagoLecturaAnteriorUseCase.execute(1L)).thenReturn(true);

        useCase.execute(dto);

        assertEquals(0.0, dto.getOtrosCobros());
    }

    @Test
    @DisplayName("Debe inicializar deuda anterior en cero si es null")
    void debeInicializarDeudaAnteriorEnCero() {
        LecturaDto dto = new LecturaDto();
        dto.setUsuarioId(1L);
        dto.setConsumo(10);
        dto.setValorCuota(0.0);
        dto.setDeudaAnterior(null);

        when(valorService.calcularValorConsumo(10)).thenReturn(15000.0);
        when(valorService.obtenerValores()).thenReturn(valores);
        when(verificarPagoLecturaAnteriorUseCase.execute(1L)).thenReturn(true);

        useCase.execute(dto);

        assertEquals(0.0, dto.getDeudaAnterior());
    }

    @Test
    @DisplayName("Debe calcular total con todos los componentes")
    void debeCalcularTotalConTodosLosComponentes() {
        LecturaDto dto = new LecturaDto();
        dto.setUsuarioId(1L);
        dto.setConsumo(10);
        dto.setOtrosCobros(3000.0);
        dto.setDeudaAnterior(5000.0);
        dto.setValorCuota(10000.0);

        when(valorService.calcularValorConsumo(10)).thenReturn(15000.0);
        when(valorService.obtenerValores()).thenReturn(valores);
        when(verificarPagoLecturaAnteriorUseCase.execute(1L)).thenReturn(false);

        useCase.execute(dto);

        assertEquals(40000.0, dto.getValorTotal());
    }

    @Test
    @DisplayName("Debe calcular correctamente con consumo cero")
    void debeCalcularConConsumoCero() {
        LecturaDto dto = new LecturaDto();
        dto.setUsuarioId(1L);
        dto.setConsumo(0);
        dto.setValorCuota(0.0);

        when(valorService.calcularValorConsumo(0)).thenReturn(0.0);
        when(valorService.obtenerValores()).thenReturn(valores);
        when(verificarPagoLecturaAnteriorUseCase.execute(1L)).thenReturn(true);

        useCase.execute(dto);

        assertEquals(0.0, dto.getValorConsumo());
        assertEquals(5000.0, dto.getValorTotal());
    }

    @Test
    @DisplayName("Debe incluir valor de cuota en el total")
    void debeIncluirValorCuotaEnTotal() {
        LecturaDto dto = new LecturaDto();
        dto.setUsuarioId(1L);
        dto.setConsumo(10);
        dto.setValorCuota(15000.0);

        when(valorService.calcularValorConsumo(10)).thenReturn(15000.0);
        when(valorService.obtenerValores()).thenReturn(valores);
        when(verificarPagoLecturaAnteriorUseCase.execute(1L)).thenReturn(true);

        useCase.execute(dto);

        assertEquals(35000.0, dto.getValorTotal());
    }
}
