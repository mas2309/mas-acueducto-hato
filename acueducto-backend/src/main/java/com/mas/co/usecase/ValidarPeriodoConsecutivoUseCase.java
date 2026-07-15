package com.mas.co.usecase;

import com.mas.co.constants.BusinessConstants;
import com.mas.co.dto.LecturaDto;
import com.mas.co.exception.BusinessException;
import com.mas.co.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Caso de Uso para validar que el período de una nueva factura sea consecutivo a la última registrada.
 */
@Component
@RequiredArgsConstructor
public class ValidarPeriodoConsecutivoUseCase {

    private final FacturaRepository facturaRepository;

    /**
     * Ejecuta la validación.
     *
     * @param lecturaDto El DTO de la lectura que se está intentando ingresar.
     * @throws BusinessException si el período no es consecutivo.
     */
    public void execute(LecturaDto lecturaDto) {
        facturaRepository.findUltimaLecturaPorUsuario(lecturaDto.getUsuarioId()).stream()
            .findFirst()
            .ifPresent(ultimaFactura -> {
                String mesAnterior = ultimaFactura.getMes();
                Integer anioAnterior = ultimaFactura.getAnio();

                // Calcular el período siguiente esperado
                int mesAnteriorInt = BusinessConstants.MONTH_TO_INT.get(mesAnterior);
                int anioSiguiente = (mesAnteriorInt == 12) ? anioAnterior + 1 : anioAnterior;
                int mesSiguienteInt = (mesAnteriorInt % 12) + 1;
                String mesSiguiente = BusinessConstants.INT_TO_MONTH.get(mesSiguienteInt);

                // Comparar con el período de la nueva lectura
                if (!mesSiguiente.equals(lecturaDto.getMes()) || !Integer.valueOf(anioSiguiente).equals(lecturaDto.getAnio())) {
                    throw BusinessException.nonSequentialInvoicePeriod(
                        lecturaDto.getMes(), lecturaDto.getAnio(), mesSiguiente, anioSiguiente
                    );
                }
            });
    }
}
