package com.mas.co.usecase;

import com.mas.co.dto.LecturaDto;
import com.mas.co.service.ValorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Caso de Uso para calcular todos los valores monetarios de una factura.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CalcularValoresFacturaUseCase {

    private final ValorService valorService;
    private final VerificarPagoLecturaAnteriorUseCase verificarPagoLecturaAnteriorUseCase;

    /**
     * Ejecuta el cálculo de todos los valores de la factura.
     *
     * @param lecturaDto El DTO de la lectura, que se enriquecerá con los valores calculados.
     */
    public void execute(LecturaDto lecturaDto) {
        log.debug("Calculando valores para consumo: {} m³", lecturaDto.getConsumo());

        // Calcular valor del consumo usando rangos
        Double valorConsumo = valorService.calcularValorConsumo(lecturaDto.getConsumo());
        lecturaDto.setValorConsumo(valorConsumo);

        // Obtener cargo fijo desde configuración
        if (lecturaDto.getCargoFijo() == null) {
            lecturaDto.setCargoFijo(valorService.obtenerValores().getCargoFijo());
        }

        // Inicializar valores opcionales que podrían ser nulos
        if (lecturaDto.getOtrosCobros() == null) {
            lecturaDto.setOtrosCobros(0.0);
        }
        if (lecturaDto.getDeudaAnterior() == null) {
            lecturaDto.setDeudaAnterior(0.0);
        }

        // Aplicar valor de no pago si la factura anterior no fue pagada
        boolean lecturaAnteriorPagada = verificarPagoLecturaAnteriorUseCase.execute(lecturaDto.getUsuarioId());
        Double valorNoPago = 0.0;
        if (!lecturaAnteriorPagada) {
            valorNoPago = valorService.obtenerValores().getNoPago();
        }
        lecturaDto.setNoPago(valorNoPago);

        // Calcular valor total sumando todos los componentes
        Double valorTotal =
            valorConsumo
                + lecturaDto.getCargoFijo()
                + lecturaDto.getOtrosCobros()
                + lecturaDto.getDeudaAnterior()
                + lecturaDto.getValorCuota()
                + lecturaDto.getNoPago();

        lecturaDto.setValorTotal(valorTotal);

        log.debug(
            "Valores calculados - Consumo: {}, Cargo fijo: {}, Total: {}",
            valorConsumo,
            lecturaDto.getCargoFijo(),
            valorTotal);
    }
}
