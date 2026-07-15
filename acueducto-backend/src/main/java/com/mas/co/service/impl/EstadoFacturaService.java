package com.mas.co.service.impl;

import com.mas.co.config.FacturacionConfig;
import com.mas.co.constants.BusinessConstants;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EstadoFacturaService {

    private static final String ESTADO_VENCIDA = "VENCIDA";

    private final FacturacionConfig facturacionConfig;

    /**
     * Determina el estado real de una factura.
     * Si no está pagada y ya pasó el día límite del mes siguiente, es VENCIDA.
     */
    public String calcularEstado(boolean pago, boolean pagoBanco, String mes, Integer anio) {
        if (pago) {
            return BusinessConstants.PAYMENT_TYPE_CASH;
        }
        if (pagoBanco) {
            return BusinessConstants.PAYMENT_TYPE_BANK;
        }
        if (estaVencida(mes, anio)) {
            return ESTADO_VENCIDA;
        }
        return BusinessConstants.PAYMENT_TYPE_NO_PAYMENT;
    }

    /**
     * Verifica si una factura está vencida.
     * La factura de un mes tiene plazo hasta el día límite del mes siguiente.
     * Ej: Factura de Enero → plazo hasta el 20 de Febrero.
     */
    public boolean estaVencida(String mes, Integer anio) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = calcularFechaLimite(mes, anio);
        return hoy.isAfter(fechaLimite);
    }

    private LocalDate calcularFechaLimite(String mes, Integer anio) {
        Integer mesNumero = BusinessConstants.MONTH_TO_INT.get(mes);
        if (mesNumero == null) {
            return LocalDate.MAX;
        }

        // El plazo es el día límite del mes SIGUIENTE
        int mesSiguiente = mesNumero + 1;
        int anioLimite = anio;
        if (mesSiguiente > 12) {
            mesSiguiente = 1;
            anioLimite++;
        }

        int diaLimite = facturacionConfig.getDiaLimitePago();
        // Ajustar si el día límite excede los días del mes
        int diasEnMes = LocalDate.of(anioLimite, mesSiguiente, 1).lengthOfMonth();
        if (diaLimite > diasEnMes) {
            diaLimite = diasEnMes;
        }

        return LocalDate.of(anioLimite, mesSiguiente, diaLimite);
    }
}
